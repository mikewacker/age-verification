package org.example.age.site.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;
import static org.example.age.testing.client.WebStageTesting.awaitErrorCode;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.function.Supplier;
import okhttp3.ResponseBody;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.testing.api.TestModels;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

public final class AvsClientTest {

    private final FakeUnderlyingAvsClient underlyingClient = new FakeUnderlyingAvsClient();
    private final AvsClient client = TestComponent.create(underlyingClient);

    @Test
    public void createVerificationRequest() {
        VerificationRequest request = await(client.createVerificationRequest());
        assertThat(request.getSiteId()).isEqualTo("site");
    }

    @Test
    public void error_CreateVerificationRequest() {
        underlyingClient.errorCode = 404;
        awaitErrorCode(client.createVerificationRequest(), 500);
    }

    /** Fake client for {@link AvsApi}. */
    private static final class FakeUnderlyingAvsClient implements AvsApi {

        int errorCode = 0;

        @Override
        public Call<VerificationRequest> createVerificationRequestForSite(String siteId) {
            if (errorCode != 0) {
                Response<VerificationRequest> response = Response.error(errorCode, ResponseBody.create("", null));
                return Calls.response(response);
            }

            VerificationRequest request = TestModels.createVerificationRequest(siteId);
            return Calls.response(request);
        }

        @Override
        public Call<Void> linkVerificationRequest(SecureId requestId) {
            return Calls.failure(new UnsupportedOperationException());
        }

        @Override
        public Call<Void> sendAgeCertificate() {
            return Calls.failure(new UnsupportedOperationException());
        }
    }

    /** Dagger component for {@link AvsClient}. */
    @Component
    @Singleton
    interface TestComponent extends Supplier<AvsClient> {

        static AvsClient create(AvsApi underlyingClient) {
            SiteEndpointConfig config = SiteEndpointConfig.builder()
                    .id("site")
                    .verifiedAccountExpiresIn(Duration.ofDays(30))
                    .build();
            return DaggerAvsClientTest_TestComponent.factory()
                    .create(underlyingClient, config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance AvsApi underlyingClient, @BindsInstance SiteEndpointConfig config);
        }
    }
}
