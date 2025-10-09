package org.example.age.testing.integration;

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
import org.junit.jupiter.api.Test;
import retrofit2.Response;

public abstract class VerificationTestTemplate {

    @Test
    public void verify() throws IOException {
        Response<VerificationRequest> requestResponse =
                siteClient().createVerificationRequest().execute();
        assertThat(requestResponse.isSuccessful()).isTrue();
        SecureId requestId = requestResponse.body().getId();

        Response<Void> linkResponse =
                avsClient().linkVerificationRequest(requestId).execute();
        assertThat(linkResponse.isSuccessful()).isTrue();

        Response<Void> sendResponse = avsClient().sendAgeCertificate().execute();
        assertThat(sendResponse.isSuccessful()).isTrue();

        Response<VerificationState> stateResponse =
                siteClient().getVerificationState().execute();
        assertThat(stateResponse.isSuccessful()).isTrue();
        VerificationState state = stateResponse.body();
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        VerifiedUser expectedUser = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI"))
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        assertThat(state.getUser()).isEqualTo(expectedUser);
    }

    protected abstract SiteApi siteClient();

    protected abstract AvsApi avsClient();
}
