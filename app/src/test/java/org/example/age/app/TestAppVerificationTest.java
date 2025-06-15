package org.example.age.app;

import static org.assertj.core.api.Assertions.assertThat;

import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import java.io.IOException;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.client.AvsApi;
import org.example.age.api.client.SiteApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.common.testing.TestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

public final class TestAppVerificationTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> siteApp = createTestApp(TestSiteApp.class, 8080);

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> avsApp = createTestApp(TestAvsApp.class, 9090);

    private static final SiteApi siteClient = createClient(8080, SiteApi.class);
    private static final AvsApi avsClient = createClient(9090, AvsApi.class);

    @Test
    public void verify() throws IOException {
        Response<VerificationRequest> requestResponse =
                siteClient.createVerificationRequest().execute();
        assertThat(requestResponse.isSuccessful()).isTrue();
        SecureId requestId = requestResponse.body().getId();

        Response<Void> linkResponse =
                avsClient.linkVerificationRequest(requestId).execute();
        assertThat(linkResponse.isSuccessful()).isTrue();

        Response<Void> sendResponse = avsClient.sendAgeCertificate().execute();
        assertThat(sendResponse.isSuccessful()).isTrue();

        Response<VerificationState> stateResponse =
                siteClient.getVerificationState().execute();
        assertThat(stateResponse.isSuccessful()).isTrue();
        VerificationState state = stateResponse.body();
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    private static DropwizardAppExtension<Configuration> createTestApp(
            Class<? extends Application<Configuration>> appType, int port) {
        return new DropwizardAppExtension<>(
                appType,
                null,
                ConfigOverride.config("server.applicationConnectors[0].port", Integer.toString(port)),
                ConfigOverride.config("server.adminConnectors[0].port", "0"));
    }

    private static <A> A createClient(int port, Class<A> apiType) {
        return TestClient.api(port, requestBuilder -> {}, apiType);
    }
}
