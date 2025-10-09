package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;

import java.util.Map;
import org.example.age.avs.api.AvsApi;
import org.example.age.common.api.VerificationRequest;
import org.example.age.service.testing.TestAvsService;
import org.example.age.service.testing.TestAvsServiceComponent;
import org.example.age.service.testing.TestSiteService;
import org.example.age.service.testing.TestSiteServiceComponent;
import org.example.age.site.api.SiteApi;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.testing.client.TestAsyncEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class ServiceVerificationTest {

    private TestSiteService siteService;
    private TestAvsService avsService;

    @BeforeEach
    public void createServices() {
        org.example.age.site.api.client.SiteApi siteClient = TestAsyncEndpoints.client(
                () -> siteService, SiteApi.class, org.example.age.site.api.client.SiteApi.class);
        org.example.age.avs.api.client.AvsApi avsClient =
                TestAsyncEndpoints.client(() -> avsService, AvsApi.class, org.example.age.avs.api.client.AvsApi.class);
        siteService = TestSiteServiceComponent.create(avsClient);
        avsService = TestAvsServiceComponent.create(Map.of("site", siteClient));
    }

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
}
