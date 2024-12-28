package org.example.age.app;

import static org.assertj.core.api.Assertions.assertThat;

import io.dropwizard.core.Configuration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import java.io.IOException;
import okhttp3.OkHttpClient;
import org.example.age.client.AgeCertificate;
import org.example.age.client.AgeRange;
import org.example.age.client.AuthMatchData;
import org.example.age.client.AvsApi;
import org.example.age.client.DigitalSignature;
import org.example.age.client.SignedAgeCertificate;
import org.example.age.client.SiteApi;
import org.example.age.client.VerificationRequest;
import org.example.age.client.VerificationState;
import org.example.age.client.VerifiedUser;
import org.example.age.client.retrofit.ApiClient;
import org.example.age.crypto.SecureId;
import org.example.age.crypto.SignatureData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

public final class E2ETest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> siteApp =
            new DropwizardAppExtension<>(SiteApp.class, ResourceHelpers.resourceFilePath("config-site.yaml"));

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> avsApp =
            new DropwizardAppExtension<>(AvsApp.class, ResourceHelpers.resourceFilePath("config-avs.yaml"));

    private static SiteApi siteClient;
    private static AvsApi avsClient;

    @BeforeAll
    public static void createClients() {
        OkHttpClient httpClient = new OkHttpClient();
        ApiClient apiClient = new ApiClient(httpClient);
        apiClient.getAdapterBuilder().baseUrl("http://localhost:8080");
        siteClient = apiClient.createService(SiteApi.class);
        apiClient.getAdapterBuilder().baseUrl("http://localhost:8081");
        avsClient = apiClient.createService(AvsApi.class);
    }

    @Test
    public void verifySiteStub() throws IOException {
        Response<VerificationState> stateResponse =
                siteClient.getVerificationState().execute();
        assertThat(stateResponse.isSuccessful()).isTrue();
        assertThat(stateResponse.body()).isNotNull();

        Response<VerificationRequest> requestResponse =
                siteClient.createVerificationRequest().execute();
        assertThat(requestResponse.isSuccessful()).isTrue();
        VerificationRequest request = requestResponse.body();
        assertThat(request).isNotNull();

        SignedAgeCertificate signedAgeCertificate = createSignedAgeCertificate(request);
        Response<Void> certificateResponse = siteClient.processAgeCertificate(signedAgeCertificate).execute();
        assertThat(certificateResponse.isSuccessful()).isTrue();
    }

    @Test
    public void verifyAvsStub() throws IOException {
        AuthMatchData authMatchData = AuthMatchData.builder().name("").data("").build();
        Response<VerificationRequest> requestResponse = avsClient.createVerificationRequestForSite("site", authMatchData).execute();
        assertThat(requestResponse.isSuccessful()).isTrue();
        VerificationRequest request = requestResponse.body();
        assertThat(request).isNotNull();

        Response<Void> linkResponse = avsClient.linkVerificationRequestToUser(request.getId()).execute();
        assertThat(linkResponse.isSuccessful()).isTrue();

        Response<Void> certificateResponse = avsClient.sendAgeCertificateForVerificationRequest(request.getId()).execute();
        assertThat(certificateResponse.isSuccessful()).isTrue();
    }

    private static SignedAgeCertificate createSignedAgeCertificate(VerificationRequest request) {
        VerifiedUser user = VerifiedUser.builder()
                .pseudonym(SecureId.generate())
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        AgeCertificate ageCertificate =
                AgeCertificate.builder().request(request).user(user).build();
        DigitalSignature signature = DigitalSignature.builder()
                .algorithm("algorithm")
                .data(SignatureData.fromString("data"))
                .build();
        return SignedAgeCertificate.builder().ageCertificate(ageCertificate).signature(signature).build();
    }
}
