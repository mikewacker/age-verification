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
import jakarta.ws.rs.NotFoundException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AgeCertificate;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.client.SiteApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.module.client.SiteClientRepository;
import org.example.age.service.module.crypto.AgeCertificateVerifier;
import org.example.age.service.testing.TestDependenciesModule;
import org.example.age.service.testing.request.TestAccountId;
import org.example.age.testing.CompletionStageTesting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.mock.Calls;

public final class AvsServiceTest {

    private static final AuthMatchData EMPTY_DATA =
            AuthMatchData.builder().name("").data("").build();

    private AvsApi avsService;
    private TestAccountId accountId;
    private static AgeCertificate ageCertificate;

    @BeforeEach
    public void createAvsServiceEtAl() {
        TestComponent component = TestComponent.create();
        avsService = component.avsService();
        accountId = component.accountId();
        ageCertificate = null;
    }

    @Test
    public void verify() throws Exception {
        CompletionStage<VerificationRequest> requestResponse1 =
                avsService.createVerificationRequestForSite("site1", EMPTY_DATA);
        assertThat(requestResponse1).isCompleted();
        VerificationRequest request1 = requestResponse1.toCompletableFuture().get();
        assertThat(request1.getSiteId()).isEqualTo("site1");
        OffsetDateTime expectedExpiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(5));
        assertThat(request1.getExpiration()).isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS));
        SecureId requestId1 = request1.getId();

        accountId.set("person");
        CompletionStage<Void> linkResponse1 = avsService.linkVerificationRequest(requestId1);
        assertThat(linkResponse1).isCompleted();

        CompletionStage<Void> sendResponse1 = avsService.sendAgeCertificate();
        assertThat(sendResponse1).isCompleted();
        assertThat(ageCertificate.getRequest()).isEqualTo(request1);
        SecureId pseudonym1 = ageCertificate.getUser().getPseudonym();
        ageCertificate = null;

        CompletionStage<VerificationRequest> requestResponse2 =
                avsService.createVerificationRequestForSite("site2", EMPTY_DATA);
        assertThat(requestResponse2).isCompleted();
        SecureId requestId2 = requestResponse2.toCompletableFuture().get().getId();

        CompletionStage<Void> linkResponse2 = avsService.linkVerificationRequest(requestId2);
        assertThat(linkResponse2).isCompleted();

        CompletionStage<Void> sendResponse2 = avsService.sendAgeCertificate();
        assertThat(sendResponse2).isCompleted();
        SecureId pseudonym2 = ageCertificate.getUser().getPseudonym();
        assertThat(pseudonym2).isNotEqualTo(pseudonym1);
    }

    @Test
    public void error_Unauthenticated() {
        CompletionStage<Void> linkResponse = avsService.linkVerificationRequest(SecureId.generate());
        assertIsCompletedWithErrorCode(linkResponse, 401);

        CompletionStage<Void> certificateResponse = avsService.sendAgeCertificate();
        assertIsCompletedWithErrorCode(certificateResponse, 401);
    }

    @Test
    public void error_UnverifiedPerson() {
        accountId.set("unverified-person");
        CompletionStage<Void> linkResponse = avsService.linkVerificationRequest(SecureId.generate());
        assertIsCompletedWithErrorCode(linkResponse, 403);

        CompletionStage<Void> certificateResponse = avsService.sendAgeCertificate();
        assertIsCompletedWithErrorCode(certificateResponse, 403);
    }

    @Test
    public void error_LinkVerificationRequestTwice() throws Exception {
        CompletionStage<VerificationRequest> requestResponse =
                avsService.createVerificationRequestForSite("site1", EMPTY_DATA);
        assertThat(requestResponse).isCompleted();
        SecureId requestId = requestResponse.toCompletableFuture().get().getId();

        accountId.set("person");
        CompletionStage<Void> linkResponse = avsService.linkVerificationRequest(requestId);
        assertThat(linkResponse).isCompleted();

        CompletionStage<Void> doubleLinkResponse = avsService.linkVerificationRequest(requestId);
        assertIsCompletedWithErrorCode(doubleLinkResponse, 404);
    }

    @Test
    public void error_SendAgeCertificateTwice() throws Exception {
        CompletionStage<VerificationRequest> requestResponse =
                avsService.createVerificationRequestForSite("site1", EMPTY_DATA);
        assertThat(requestResponse).isCompleted();
        SecureId requestId = requestResponse.toCompletableFuture().get().getId();

        accountId.set("person");
        CompletionStage<Void> linkResponse = avsService.linkVerificationRequest(requestId);
        assertThat(linkResponse).isCompleted();

        CompletionStage<Void> sendResponse = avsService.sendAgeCertificate();
        assertThat(sendResponse).isCompleted();

        CompletionStage<Void> doubleSendResponse = avsService.sendAgeCertificate();
        assertIsCompletedWithErrorCode(doubleSendResponse, 404);
    }

    @Test
    public void error_VerificationRequestNotFound() {
        accountId.set("person");
        CompletionStage<Void> linkResponse = avsService.linkVerificationRequest(SecureId.generate());
        assertIsCompletedWithErrorCode(linkResponse, 404);

        CompletionStage<Void> sendResponse = avsService.sendAgeCertificate();
        assertIsCompletedWithErrorCode(sendResponse, 404);
    }

    @Test
    public void error_UnregisteredSite() {
        CompletionStage<VerificationRequest> requestResponse =
                avsService.createVerificationRequestForSite("unregistered-site", EMPTY_DATA);
        assertIsCompletedWithErrorCode(requestResponse, 404);
    }

    /** Fake implementation of {@link SiteClientRepository}. */
    @Singleton
    static final class FakeSiteClientRepository implements SiteClientRepository {

        private static final Set<String> SITE_IDS = Set.of("site1", "site2");

        private final SiteApi siteClient;

        @Inject
        public FakeSiteClientRepository(FakeSiteClient siteClient) {
            this.siteClient = siteClient;
        }

        @Override
        public SiteApi get(String siteId) {
            if (!SITE_IDS.contains(siteId)) {
                throw new NotFoundException();
            }

            return siteClient;
        }
    }

    /** Fake client implementation of {@link SiteApi}. */
    @Singleton
    static final class FakeSiteClient implements SiteApi {

        private final AgeCertificateVerifier ageCertificateVerifier;

        @Inject
        public FakeSiteClient(AgeCertificateVerifier ageCertificateVerifier) {
            this.ageCertificateVerifier = ageCertificateVerifier;
        }

        @Override
        public Call<VerificationState> getVerificationState() {
            return Calls.failure(new UnsupportedOperationException());
        }

        @Override
        public Call<VerificationRequest> createVerificationRequest() {
            return Calls.failure(new UnsupportedOperationException());
        }

        @Override
        public Call<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
            CompletionStage<Void> response = ageCertificateVerifier
                    .verify(signedAgeCertificate)
                    .thenAccept(ageCertificate -> AvsServiceTest.ageCertificate = ageCertificate);
            return CompletionStageTesting.toCall(response);
        }
    }

    /** Dagger component for the service. */
    @Component(modules = {AvsServiceModule.class, FakeClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerAvsServiceTest_TestComponent.create();
        }

        @Named("service")
        AvsApi avsService();

        TestAccountId accountId();
    }

    /** Dagger module that binds {@link SiteClientRepository}. */
    @Module
    interface FakeClientModule {

        @Binds
        SiteClientRepository bindSiteClientRepository(FakeSiteClientRepository impl);
    }
}
