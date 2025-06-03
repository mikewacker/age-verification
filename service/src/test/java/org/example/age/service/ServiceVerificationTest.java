package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;

import jakarta.ws.rs.NotFoundException;
import java.util.Map;
import java.util.Optional;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.crypto.SecureId;
import org.example.age.common.testing.WebStageTesting;
import org.example.age.service.testing.TestAvsServiceComponent;
import org.example.age.service.testing.TestSiteServiceComponent;
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
        TestSiteServiceComponent siteComponent = TestSiteServiceComponent.create(new AdaptedAvsClient());
        siteService = siteComponent.service();
        siteAccountId = siteComponent.accountId();
        TestAvsServiceComponent avsComponent = TestAvsServiceComponent.create(AdaptedSiteClient::get);
        avsService = avsComponent.service();
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
    private static final class AdaptedAvsClient implements org.example.age.api.client.AvsApi {

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

    /** Adapts {@link SiteApi} to the corresponding client interface. */
    private static final class AdaptedSiteClient implements org.example.age.api.client.SiteApi {

        private static final Map<String, org.example.age.api.client.SiteApi> clients =
                Map.of("site1", new AdaptedSiteClient());

        public static org.example.age.api.client.SiteApi get(String siteId) {
            return Optional.ofNullable(clients.get(siteId)).orElseThrow(NotFoundException::new);
        }

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
}
