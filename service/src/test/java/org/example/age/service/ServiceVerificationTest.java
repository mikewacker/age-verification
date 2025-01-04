package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.api.client.SiteClientRepository;
import org.example.age.service.testing.TestServiceDependenciesModule;
import org.example.age.service.testing.request.TestAccountId;
import org.example.age.testing.CompletionStageTesting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

public final class ServiceVerificationTest {

    private static SiteApi siteService;
    private TestAccountId siteAccountId;

    private static AvsApi avsService;
    private TestAccountId avsAccountId;

    @BeforeEach
    public void createServicesEtAl() {
        TestSiteComponent siteComponent = TestSiteComponent.create();
        siteService = siteComponent.service();
        siteAccountId = siteComponent.accountId();
        TestAvsComponent avsComponent = TestAvsComponent.create();
        avsService = avsComponent.service();
        avsAccountId = avsComponent.accountId();
    }

    @Test
    public void verify() throws Exception {
        siteAccountId.set("username");
        CompletionStage<VerificationRequest> requestResponse = siteService.createVerificationRequest();
        assertThat(requestResponse).isCompleted();
        SecureId requestId = requestResponse.toCompletableFuture().get().getId();

        avsAccountId.set("person");
        CompletionStage<Void> linkResponse = avsService.linkVerificationRequest(requestId);
        assertThat(linkResponse).isCompleted();

        CompletionStage<Void> sendResponse = avsService.sendAgeCertificate();
        assertThat(sendResponse).isCompleted();

        CompletionStage<VerificationState> stateResponse = siteService.getVerificationState();
        assertThat(stateResponse).isCompleted();
        VerificationStatus status = stateResponse.toCompletableFuture().get().getStatus();
        assertThat(status).isEqualTo(VerificationStatus.VERIFIED);
    }

    /** Adapts {@link AvsApi} to the corresponding client interface. */
    @Singleton
    static final class AdaptedAvsClient implements org.example.age.api.client.AvsApi {

        @Inject
        public AdaptedAvsClient() {}

        @Override
        public Call<VerificationRequest> createVerificationRequestForSite(String siteId, AuthMatchData authMatchData) {
            return CompletionStageTesting.toCall(avsService.createVerificationRequestForSite(siteId, authMatchData));
        }

        @Override
        public Call<Void> linkVerificationRequest(SecureId requestId) {
            return CompletionStageTesting.toCall(avsService.linkVerificationRequest(requestId));
        }

        @Override
        public Call<Void> sendAgeCertificate() {
            return CompletionStageTesting.toCall(avsService.sendAgeCertificate());
        }
    }

    /** Fake implementation of {@link SiteClientRepository}. */
    @Singleton
    static final class FakeSiteClientRepository implements SiteClientRepository {

        private final org.example.age.api.client.SiteApi siteClient;

        @Inject
        public FakeSiteClientRepository(AdaptedSiteClient siteClient) {
            this.siteClient = siteClient;
        }

        @Override
        public org.example.age.api.client.SiteApi get(String siteId) {
            if (!siteId.equals("site")) {
                throw new NotAuthorizedException("unregistered site");
            }

            return siteClient;
        }
    }

    /** Adapts {@link SiteApi} to the corresponding client interface. */
    @Singleton
    static final class AdaptedSiteClient implements org.example.age.api.client.SiteApi {

        @Inject
        public AdaptedSiteClient() {}

        @Override
        public Call<VerificationState> getVerificationState() {
            return CompletionStageTesting.toCall(siteService.getVerificationState());
        }

        @Override
        public Call<VerificationRequest> createVerificationRequest() {
            return CompletionStageTesting.toCall(siteService.createVerificationRequest());
        }

        @Override
        public Call<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
            return CompletionStageTesting.toCall(siteService.processAgeCertificate(signedAgeCertificate));
        }
    }

    /** Dagger component for the site service. */
    @Component(modules = {SiteServiceModule.class, TestSiteClientModule.class, TestServiceDependenciesModule.class})
    @Singleton
    interface TestSiteComponent {

        static TestSiteComponent create() {
            return DaggerServiceVerificationTest_TestSiteComponent.create();
        }

        @Named("service")
        SiteApi service();

        TestAccountId accountId();
    }

    /** Dagger component for the AVS service. */
    @Component(modules = {AvsServiceModule.class, TestAvsClientModule.class, TestServiceDependenciesModule.class})
    @Singleton
    interface TestAvsComponent {

        static TestAvsComponent create() {
            return DaggerServiceVerificationTest_TestAvsComponent.create();
        }

        @Named("service")
        AvsApi service();

        TestAccountId accountId();
    }

    /** Dagger module that binds <code>@Named("client") {@link org.example.age.api.client.AvsApi}</code>. */
    @Module
    interface TestSiteClientModule {

        @Binds
        @Named("client")
        org.example.age.api.client.AvsApi bindAvsClient(AdaptedAvsClient client);
    }

    /** Dagger module that binds {@link SiteClientRepository}. */
    @Module
    interface TestAvsClientModule {

        @Binds
        SiteClientRepository bindSiteClientRepository(FakeSiteClientRepository impl);
    }
}
