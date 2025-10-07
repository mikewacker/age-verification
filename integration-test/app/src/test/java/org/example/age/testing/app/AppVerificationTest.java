package org.example.age.testing.app;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.site.api.client.SiteApi;
import org.example.age.testing.client.TestClient;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public final class AppVerificationTest {

    private static final SiteApi siteClient = createClient(8080, "username", SiteApi.class);
    private static final AvsApi avsClient = createClient(9090, "person", AvsApi.class);

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
        VerifiedUser expectedUser = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI"))
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        assertThat(state.getUser()).isEqualTo(expectedUser);
    }

    private static <A> A createClient(int port, String accountId, Class<A> apiType) {
        return TestClient.api(port, requestBuilder -> requestBuilder.header("Account-Id", accountId), apiType);
    }
}
