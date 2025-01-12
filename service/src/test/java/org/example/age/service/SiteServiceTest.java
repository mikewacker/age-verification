package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.example.age.testing.CompletionStageTesting.assertIsCompletedWithErrorCode;
import static org.example.age.testing.CompletionStageTesting.getCompleted;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AgeCertificate;
import org.example.age.api.AuthMatchData;
import org.example.age.api.DigitalSignature;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.client.AvsApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.api.crypto.SignatureData;
import org.example.age.service.module.crypto.AgeCertificateSigner;
import org.example.age.service.testing.TestDependenciesModule;
import org.example.age.service.testing.request.TestAccountId;
import org.example.age.testing.TestModels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.mock.Calls;

public final class SiteServiceTest {

    private SiteApi siteService;
    private TestAccountId accountId;
    private AgeCertificateSigner ageCertificateSigner;

    @BeforeEach
    public void createSiteServiceEtAl() {
        TestComponent component = TestComponent.create();
        siteService = component.siteService();
        accountId = component.accountId();
        ageCertificateSigner = component.ageCertificateSigner();
    }

    @Test
    public void verify() {
        accountId.set("username");
        CompletionStage<VerificationState> initStateResponse = siteService.getVerificationState();
        VerificationState expectedInitState = VerificationState.builder()
                .status(VerificationStatus.UNVERIFIED)
                .build();
        assertThat(initStateResponse).isCompletedWithValue(expectedInitState);

        CompletionStage<VerificationRequest> requestResponse = siteService.createVerificationRequest();
        assertThat(requestResponse).isCompleted();
        VerificationRequest request = getCompleted(requestResponse);
        assertThat(request.getSiteId()).isEqualTo("site");

        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        SignedAgeCertificate signedAgeCertificate = sign(ageCertificate);
        CompletionStage<Void> certificateResponse = siteService.processAgeCertificate(signedAgeCertificate);
        assertThat(certificateResponse).isCompleted();

        CompletionStage<VerificationState> stateResponse = siteService.getVerificationState();
        assertThat(stateResponse).isCompleted();
        VerificationState state = getCompleted(stateResponse);
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.getUser().getPseudonym())
                .isNotEqualTo(signedAgeCertificate.getAgeCertificate().getUser().getPseudonym());
        OffsetDateTime expectedExpiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofDays(30));
        assertThat(state.getExpiration()).isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void error_DuplicateVerification() {
        accountId.set("duplicate");
        CompletionStage<VerificationRequest> requestResponse = siteService.createVerificationRequest();
        assertThat(requestResponse).isCompleted();
        VerificationRequest request = getCompleted(requestResponse);
        assertThat(request.getSiteId()).isEqualTo("site");

        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        SignedAgeCertificate signedAgeCertificate = sign(ageCertificate);
        CompletionStage<Void> certificateResponse = siteService.processAgeCertificate(signedAgeCertificate);
        assertIsCompletedWithErrorCode(certificateResponse, 409);
    }

    @Test
    public void error_Unauthenticated() {
        CompletionStage<VerificationState> stateResponse = siteService.getVerificationState();
        assertIsCompletedWithErrorCode(stateResponse, 401);

        CompletionStage<VerificationRequest> requestResponse = siteService.createVerificationRequest();
        assertIsCompletedWithErrorCode(requestResponse, 401);
    }

    @Test
    public void error_AccountNotFound() {
        VerificationRequest request = TestModels.createVerificationRequest("site");
        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        SignedAgeCertificate signedAgeCertificate = sign(ageCertificate);
        CompletionStage<Void> certificateResponse = siteService.processAgeCertificate(signedAgeCertificate);
        assertIsCompletedWithErrorCode(certificateResponse, 404);
    }

    @Test
    public void error_ExpiredAgeCertificate() {
        VerificationRequest request = createExpiredVerificationRequest();
        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        SignedAgeCertificate signedAgeCertificate = sign(ageCertificate);
        CompletionStage<Void> certificateResponse = siteService.processAgeCertificate(signedAgeCertificate);
        assertIsCompletedWithErrorCode(certificateResponse, 404);
    }

    @Test
    public void error_InvalidSignature() {
        VerificationRequest request = TestModels.createVerificationRequest("site");
        AgeCertificate ageCertificate = TestModels.createAgeCertificate(request);
        SignedAgeCertificate signedAgeCertificate = signInvalid(ageCertificate);
        CompletionStage<Void> certificateResponse = siteService.processAgeCertificate(signedAgeCertificate);
        assertIsCompletedWithErrorCode(certificateResponse, 401);
    }

    private SignedAgeCertificate sign(AgeCertificate ageCertificate) {
        return getCompleted(ageCertificateSigner.sign(ageCertificate));
    }

    private static SignedAgeCertificate signInvalid(AgeCertificate ageCertificate) {
        DigitalSignature signature = DigitalSignature.builder()
                .algorithm("secp256r1")
                .data(SignatureData.fromString(""))
                .build();
        return SignedAgeCertificate.builder()
                .ageCertificate(ageCertificate)
                .signature(signature)
                .build();
    }

    private static VerificationRequest createExpiredVerificationRequest() {
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(-5));
        return VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId("site")
                .expiration(expiration)
                .build();
    }

    /** Fake client implementation of {@link AvsApi}. */
    @Singleton
    static final class FakeAvsClient implements AvsApi {

        @Inject
        public FakeAvsClient() {}

        @Override
        public Call<VerificationRequest> createVerificationRequestForSite(String siteId, AuthMatchData authMatchData) {
            VerificationRequest request = TestModels.createVerificationRequest(siteId);
            return Calls.response(request);
        }

        @Override
        public Call<Void> linkVerificationRequest(SecureId requestId) {
            return Calls.failure(new UnsupportedOperationException());
        }

        @Override
        public Call<Void> sendAgeCertificate() {
            return Calls.failure(new UnsupportedOperationException());
        }
    }

    /** Dagger component for the service. */
    @Component(modules = {SiteServiceModule.class, FakeClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerSiteServiceTest_TestComponent.create();
        }

        @Named("service")
        SiteApi siteService();

        TestAccountId accountId();

        AgeCertificateSigner ageCertificateSigner();
    }

    /** Dagger module that binds <code>@Named("client") {@link AvsApi}</code>. */
    @Module
    interface FakeClientModule {

        @Binds
        @Named("client")
        AvsApi bindAvsClient(FakeAvsClient client);
    }
}
