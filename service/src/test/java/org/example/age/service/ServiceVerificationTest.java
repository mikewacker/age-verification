package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.util.WebStageTesting.await;

import jakarta.ws.rs.NotFoundException;
import java.util.Map;
import java.util.Optional;
import org.example.age.avs.api.AvsApi;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.service.testing.TestAvsService;
import org.example.age.service.testing.TestAvsServiceComponent;
import org.example.age.service.testing.TestSiteService;
import org.example.age.service.testing.TestSiteServiceComponent;
import org.example.age.site.api.SiteApi;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.testing.util.WebStageTesting;
import org.junit.jupiter.api.Test;
import retrofit2.Call;

public final class ServiceVerificationTest {

    private final TestSiteService siteService = TestSiteServiceComponent.create(new AdaptedAvsClient());
    private final TestAvsService avsService = TestAvsServiceComponent.create(this::getSiteClient);

    private final Map<String, org.example.age.site.api.client.SiteApi> siteClients =
            Map.of("site1", new AdaptedSiteClient());

    @Test
    public void verify() {
        siteService.setAccountId("username");
        avsService.setAccountId("person");
        VerificationRequest request = await(siteService.createVerificationRequest());
        await(avsService.linkVerificationRequest(request.getId()));
        await(avsService.sendAgeCertificate());
        VerificationState state = await(siteService.getVerificationState());
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    private org.example.age.site.api.client.SiteApi getSiteClient(String siteId) {
        return Optional.ofNullable(siteClients.get(siteId)).orElseThrow(NotFoundException::new);
    }

    /** Adapts {@link AvsApi} to the corresponding client interface. */
    private final class AdaptedAvsClient implements org.example.age.avs.api.client.AvsApi {

        @Override
        public Call<VerificationRequest> createVerificationRequestForSite(String siteId) {
            return WebStageTesting.toCall(avsService.createVerificationRequestForSite(siteId));
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
    private final class AdaptedSiteClient implements org.example.age.site.api.client.SiteApi {

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
