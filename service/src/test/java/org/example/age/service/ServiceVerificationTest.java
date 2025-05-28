package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.crypto.SecureId;
import org.example.age.common.testing.WebStageTesting;
import org.example.age.service.module.client.SiteClientRepository;
import org.example.age.service.testing.TestDependenciesModule;
import org.example.age.service.testing.TestWrappedAvsService;
import org.example.age.service.testing.TestWrappedSiteService;
import org.example.age.service.testing.request.TestAccountId;
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
        siteService = new TestWrappedSiteService(siteComponent.service());
        siteAccountId = siteComponent.accountId();
        TestAvsComponent avsComponent = TestAvsComponent.create();
        avsService = new TestWrappedAvsService(avsComponent.service());
        avsAccountId = avsComponent.accountId();
    }

    @Test
    public void verify() {
        siteAccountId.set("username");
        avsAccountId.set("person");
        VerificationRequest request = await(siteService.createVerificationRequest());
        await(avsService.linkVerificationRequest(request.getId()));
        await(avsService.sendAgeCertificate());
        VerificationState state = await(siteService.getVerificationState());
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    /** Adapts {@link AvsApi} to the corresponding client interface. */
    @Singleton
    static final class AdaptedAvsClient implements org.example.age.api.client.AvsApi {

        @Inject
        public AdaptedAvsClient() {}

        @Override
        public Call<VerificationRequest> createVerificationRequestForSite(String siteId, AuthMatchData authMatchData) {
            return WebStageTesting.toCall(avsService.createVerificationRequestForSite(siteId, authMatchData));
        }

        @Override
        public Call<Void> linkVerificationRequest(SecureId requestId) {
            return WebStageTesting.toCall(avsService.linkVerificationRequest(requestId));
        }

        @Override
        public Call<Void> sendAgeCertificate() {
            return WebStageTesting.toCall(avsService.sendAgeCertificate());
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
            if (!siteId.equals("site1")) {
                throw new NotFoundException();
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
            return WebStageTesting.toCall(siteService.getVerificationState());
        }

        @Override
        public Call<VerificationRequest> createVerificationRequest() {
            return WebStageTesting.toCall(siteService.createVerificationRequest());
        }

        @Override
        public Call<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
            return WebStageTesting.toCall(siteService.processAgeCertificate(signedAgeCertificate));
        }
    }

    /** Dagger component for the site service. */
    @Component(modules = {SiteServiceModule.class, TestSiteClientModule.class, TestDependenciesModule.class})
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
    @Component(modules = {AvsServiceModule.class, TestAvsClientModule.class, TestDependenciesModule.class})
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
