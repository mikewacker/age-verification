package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.example.age.common.testing.WebStageTesting.await;
import static org.example.age.common.testing.WebStageTesting.awaitErrorCode;

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
import org.example.age.api.AgeCertificate;
import org.example.age.api.AgeRange;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.client.SiteApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.api.testing.TestSignatures;
import org.example.age.service.module.client.SiteClientRepository;
import org.example.age.service.testing.TestDependenciesModule;
import org.example.age.service.testing.TestWrappedAvsService;
import org.example.age.service.testing.request.TestAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;
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
        avsService = new TestWrappedAvsService(component.avsService());
        accountId = component.accountId();
        ageCertificate = null;
    }

    @Test
    public void verify() {
        accountId.set("person");
        VerificationRequest request1 = await(avsService.createVerificationRequestForSite("site1", EMPTY_DATA));
        assertThat(request1.getSiteId()).isEqualTo("site1");
        OffsetDateTime expectedExpiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(5));
        assertThat(request1.getExpiration()).isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS));

        await(avsService.linkVerificationRequest(request1.getId()));
        await(avsService.sendAgeCertificate());
        assertThat(ageCertificate.getRequest()).isEqualTo(request1);
        SecureId pseudonym1 = ageCertificate.getUser().getPseudonym();
        assertThat(ageCertificate.getUser().getAgeRange())
                .isEqualTo(AgeRange.builder().min(18).build());

        ageCertificate = null;
        VerificationRequest request2 = await(avsService.createVerificationRequestForSite("site2", EMPTY_DATA));
        await(avsService.linkVerificationRequest(request2.getId()));
        await(avsService.sendAgeCertificate());
        SecureId pseudonym2 = ageCertificate.getUser().getPseudonym();
        assertThat(pseudonym2).isNotEqualTo(pseudonym1);
    }

    @Test
    public void error_Unauthenticated() {
        awaitErrorCode(avsService.linkVerificationRequest(SecureId.generate()), 401);
        awaitErrorCode(avsService.sendAgeCertificate(), 401);
    }

    @Test
    public void error_UnverifiedPerson() {
        accountId.set("unverified-person");
        awaitErrorCode(avsService.linkVerificationRequest(SecureId.generate()), 403);
        awaitErrorCode(avsService.sendAgeCertificate(), 403);
    }

    @Test
    public void error_LinkVerificationRequestTwice() {
        accountId.set("person");
        VerificationRequest request = await(avsService.createVerificationRequestForSite("site1", EMPTY_DATA));
        await(avsService.linkVerificationRequest(request.getId()));
        awaitErrorCode(avsService.linkVerificationRequest(request.getId()), 404);
    }

    @Test
    public void error_SendAgeCertificateTwice() {
        accountId.set("person");
        VerificationRequest request = await(avsService.createVerificationRequestForSite("site1", EMPTY_DATA));
        await(avsService.linkVerificationRequest(request.getId()));
        await(avsService.sendAgeCertificate());
        awaitErrorCode(avsService.sendAgeCertificate(), 404);
    }

    @Test
    public void error_VerificationRequestNotFound() {
        accountId.set("person");
        awaitErrorCode(avsService.linkVerificationRequest(SecureId.generate()), 404);
        awaitErrorCode(avsService.sendAgeCertificate(), 404);
    }

    @Test
    public void error_UnregisteredSite() {
        accountId.set("person");
        awaitErrorCode(avsService.createVerificationRequestForSite("unregistered-site", EMPTY_DATA), 404);
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

        @Inject
        public FakeSiteClient() {}

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
            ageCertificate = TestSignatures.verify(signedAgeCertificate);
            return Calls.response(Response.success(null));
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
