package org.example.age.avs.endpoint;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.example.age.testing.client.WebStageTesting.await;
import static org.example.age.testing.client.WebStageTesting.awaitErrorCode;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import java.util.Map;
import java.util.function.Supplier;
import okhttp3.ResponseBody;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.client.SiteApi;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.api.TestSignatures;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

public final class SiteClientTest {

    private final FakeUnderlyingSiteClient underlyingClient = new FakeUnderlyingSiteClient();
    private final SiteClient.Repository clients = TestComponent.create(underlyingClient);

    @Test
    public void processAgeCertificate() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        SignedAgeCertificate signedAgeCertificate = TestSignatures.sign(ageCertificate);
        await(clients.get("site").processAgeCertificate(signedAgeCertificate));
    }

    @Test
    public void error_ProcessAgeCertificate_NotFound() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        SignedAgeCertificate signedAgeCertificate = TestSignatures.sign(ageCertificate);
        underlyingClient.errorCode = 404;
        awaitErrorCode(clients.get("site").processAgeCertificate(signedAgeCertificate), 404);
    }

    @Test
    public void error_ProcessAgeCertificate_Other() {
        AgeCertificate ageCertificate = TestModels.createAgeCertificate();
        SignedAgeCertificate signedAgeCertificate = TestSignatures.sign(ageCertificate);
        underlyingClient.errorCode = 409;
        awaitErrorCode(clients.get("site").processAgeCertificate(signedAgeCertificate), 500);
    }

    @Test
    public void error_Get() {
        assertThatThrownBy(() -> clients.get("unregistered-site")).isInstanceOf(NotFoundException.class);
    }

    /** Fake client for {@link SiteApi}. */
    private static final class FakeUnderlyingSiteClient implements SiteApi {

        int errorCode = 0;

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
            if (errorCode != 0) {
                Response<Void> response = Response.error(errorCode, ResponseBody.create("", null));
                return Calls.response(response);
            }

            TestSignatures.verify(signedAgeCertificate);
            return Calls.response(Response.success(null));
        }
    }

    /** Dagger component for {@link SiteClient.Repository}. */
    @Component
    @Singleton
    interface TestComponent extends Supplier<SiteClient.Repository> {

        static SiteClient.Repository create(SiteApi underlyingClient) {
            Map<String, SiteApi> underlyingClients = Map.of("site", underlyingClient);
            return DaggerSiteClientTest_TestComponent.factory()
                    .create(underlyingClients)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance Map<String, SiteApi> underlyingClients);
        }
    }
}
