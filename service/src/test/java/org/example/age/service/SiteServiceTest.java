package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.example.age.testing.CompletionStageTesting.assertIsCompletedWithErrorCode;

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
import org.example.age.api.AgeRange;
import org.example.age.api.AuthMatchData;
import org.example.age.api.DigitalSignature;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.VerifiedUser;
import org.example.age.api.client.AvsApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.api.crypto.SignatureData;
import org.example.age.service.api.crypto.AgeCertificateSigner;
import org.example.age.service.testing.TestServiceDependenciesModule;
import org.example.age.service.testing.request.TestAccountId;
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
    public void verify() throws Exception {
        accountId.set("username");

        CompletionStage<VerificationState> initStateResponse = siteService.getVerificationState();
        VerificationState expectedInitState = VerificationState.builder()
                .status(VerificationStatus.UNVERIFIED)
                .build();
        assertThat(initStateResponse).isCompletedWithValue(expectedInitState);

        CompletionStage<VerificationRequest> requestResponse = siteService.createVerificationRequest();
        assertThat(requestResponse).isCompleted();
        VerificationRequest request = requestResponse.toCompletableFuture().get();
        assertThat(request.getSiteId()).isEqualTo("site");

        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        CompletionStage<Void> certificateResponse = siteService.processAgeCertificate(signedAgeCertificate);
        assertThat(certificateResponse).isCompleted();

        CompletionStage<VerificationState> stateResponse = siteService.getVerificationState();
        assertThat(stateResponse).isCompleted();
        VerificationState state = stateResponse.toCompletableFuture().get();
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.getUser().getPseudonym())
                .isNotEqualTo(signedAgeCertificate.getAgeCertificate().getUser().getPseudonym());
        OffsetDateTime expectedExpiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofDays(30));
        assertThat(state.getExpiration()).isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS));
    }

    @Test
    public void error_DuplicateVerification() throws Exception {
        accountId.set("duplicate");

        CompletionStage<VerificationRequest> requestResponse = siteService.createVerificationRequest();
        assertThat(requestResponse).isCompleted();
        VerificationRequest request = requestResponse.toCompletableFuture().get();
        assertThat(request.getSiteId()).isEqualTo("site");

        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        CompletionStage<Void> certificateResponse = siteService.processAgeCertificate(signedAgeCertificate);
        assertIsCompletedWithErrorCode(certificateResponse, 409);
    }

    @Test
    public void error_AccountNotFound() throws Exception {
        accountId.set("username");

        VerificationRequest request = createVerificationRequest();
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        CompletionStage<Void> certificateResponse = siteService.processAgeCertificate(signedAgeCertificate);
        assertIsCompletedWithErrorCode(certificateResponse, 404);
    }

    @Test
    public void error_ExpiredAgeCertificate() throws Exception {
        accountId.set("username");

        VerificationRequest request = createVerificationRequest(Duration.ofMinutes(-1));
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        CompletionStage<Void> certificateResponse = siteService.processAgeCertificate(signedAgeCertificate);
        assertIsCompletedWithErrorCode(certificateResponse, 410);
    }

    @Test
    public void error_InvalidSignature() throws Exception {
        accountId.set("username");

        VerificationRequest request = createVerificationRequest();
        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        SignedAgeCertificate invalidAgeCertificate = createInvalidAgeCertificate(signedAgeCertificate);
        CompletionStage<Void> certificateResponse = siteService.processAgeCertificate(invalidAgeCertificate);
        assertIsCompletedWithErrorCode(certificateResponse, 401);
    }

    private SignedAgeCertificate createSignedAgeCertificate(VerificationRequest request) throws Exception {
        VerifiedUser user = VerifiedUser.builder()
                .pseudonym(SecureId.generate())
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        AgeCertificate ageCertificate =
                AgeCertificate.builder().request(request).user(user).build();
        return ageCertificateSigner.sign(ageCertificate).toCompletableFuture().get();
    }

    private static VerificationRequest createVerificationRequest() {
        return createVerificationRequest(Duration.ofMinutes(5));
    }

    private static VerificationRequest createVerificationRequest(Duration expiresIn) {
        return VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId("site")
                .expiration(OffsetDateTime.now(ZoneOffset.UTC).plus(expiresIn))
                .build();
    }

    private static SignedAgeCertificate createInvalidAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        DigitalSignature invalidSignature = DigitalSignature.builder()
                .algorithm("")
                .data(SignatureData.fromString(""))
                .build();
        return SignedAgeCertificate.builder()
                .ageCertificate(signedAgeCertificate.getAgeCertificate())
                .signature(invalidSignature)
                .build();
    }

    /** Fake client implementation of {@link AvsApi}. */
    @Singleton
    static final class FakeAvsClient implements AvsApi {

        @Inject
        public FakeAvsClient() {}

        @Override
        public Call<VerificationRequest> createVerificationRequestForSite(String siteId, AuthMatchData authMatchData) {
            OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(5));
            VerificationRequest request = VerificationRequest.builder()
                    .id(SecureId.generate())
                    .siteId(siteId)
                    .expiration(expiration)
                    .build();
            return Calls.response(request);
        }

        @Override
        public Call<Void> linkVerificationRequestToUser(SecureId requestId) {
            return Calls.failure(new UnsupportedOperationException());
        }

        @Override
        public Call<Void> sendAgeCertificateForVerificationRequest(SecureId requestId) {
            return Calls.failure(new UnsupportedOperationException());
        }
    }

    /**
     * Dagger component that provides...
     * <ul>
     *     <li>{@link SiteApi}
     *     <li>{@link TestAccountId}
     *     <li>{@link AgeCertificateSigner}
     * </ul>
     */
    @Component(modules = {SiteServiceModule.class, FakeAvsClientModule.class, TestServiceDependenciesModule.class})
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
    interface FakeAvsClientModule {

        @Binds
        @Named("client")
        AvsApi bindAvsClient(FakeAvsClient client);
    }
}
