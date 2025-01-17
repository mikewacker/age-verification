package org.example.age.app;

import static org.assertj.core.api.Assertions.assertThat;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import java.io.IOException;
import org.example.age.api.AgeRange;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.client.AvsApi;
import org.example.age.api.client.SiteApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.app.config.AvsAppConfig;
import org.example.age.app.config.SiteAppConfig;
import org.example.age.app.testing.TestServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

public final class AppVerificationTest {

    @RegisterExtension
    private static final DropwizardAppExtension<SiteAppConfig> siteApp =
            new DropwizardAppExtension<>(SiteApp.class, ResourceHelpers.resourceFilePath("config-site.yaml"));

    @RegisterExtension
    private static final DropwizardAppExtension<AvsAppConfig> avsApp =
            new DropwizardAppExtension<>(AvsApp.class, ResourceHelpers.resourceFilePath("config-avs.yaml"));

    @RegisterExtension
    private static final TestServiceClient<SiteApi> siteClient =
            new TestServiceClient<>(8080, "username", SiteApi.class);

    @RegisterExtension
    private static final TestServiceClient<AvsApi> avsClient = new TestServiceClient<>(9090, "person", AvsApi.class);

    @Test
    public void verify() throws IOException {
        Response<VerificationRequest> requestResponse =
                siteClient.get().createVerificationRequest().execute();
        assertThat(requestResponse.isSuccessful()).isTrue();
        SecureId requestId = requestResponse.body().getId();

        Response<Void> linkResponse =
                avsClient.get().linkVerificationRequest(requestId).execute();
        assertThat(linkResponse.isSuccessful()).isTrue();

        Response<Void> sendResponse = avsClient.get().sendAgeCertificate().execute();
        assertThat(sendResponse.isSuccessful()).isTrue();

        Response<VerificationState> stateResponse =
                siteClient.get().getVerificationState().execute();
        assertThat(stateResponse.isSuccessful()).isTrue();
        VerificationState state = stateResponse.body();
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.getUser().getPseudonym())
                .isEqualTo(SecureId.fromString("wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI"));
        assertThat(state.getUser().getAgeRange())
                .isEqualTo(AgeRange.builder().min(18).build());
    }
}
