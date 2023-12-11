package org.example.age.service.site.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.BindsInstance;
import dagger.Component;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.common.VerificationState;
import org.example.age.api.common.VerificationStatus;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.avs.endpoint.test.TestFakeAvsServiceModule;
import org.example.age.service.site.endpoint.test.TestSiteServiceModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteServiceEndpointTest {

    @RegisterExtension
    private static final TestServer<?> siteServer =
            TestUndertowServer.register("site", TestComponent::createApiHandler, "/api/");

    @RegisterExtension
    private static final TestServer<?> fakeAvsServer =
            TestUndertowServer.register("avs", FakeAvsComponent::createApiHandler, "/api/");

    @Test
    public void verify() throws IOException {
        verify("publius-jr", "Billy Smith", 200);
    }

    @Test
    public void verifyFailed_DuplicateVerification() throws IOException {
        verify("publius", "John Smith", 200);
        verify("drop-table", "John Smith", 409);
    }

    private void verify(String siteAccountId, String avsAccountId, int expectedStatusCode) throws IOException {
        HttpOptional<VerificationSession> maybeSession = TestClient.apiRequestBuilder()
                .post(siteServer.url("/api/verification-session"))
                .headers(Map.of("Account-Id", siteAccountId))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeSession).isPresent();
        VerificationSession session = maybeSession.get();

        SecureId requestId = session.verificationRequest().id();
        int linkStatusCode = TestClient.apiRequestBuilder()
                .post(fakeAvsServer.url("/api/linked-verification-request?request-id=%s", requestId))
                .headers(Map.of("Account-Id", avsAccountId))
                .executeWithStatusCodeResponse();
        assertThat(linkStatusCode).isEqualTo(200);

        int certificateStatusCode = TestClient.apiRequestBuilder()
                .post(fakeAvsServer.url("/api/age-certificate"))
                .headers(Map.of("Account-Id", avsAccountId))
                .executeWithStatusCodeResponse();
        assertThat(certificateStatusCode).isEqualTo(expectedStatusCode);
        if (expectedStatusCode != 200) {
            return;
        }

        HttpOptional<VerificationState> maybeState = TestClient.apiRequestBuilder()
                .get(siteServer.url("/api/verification-state"))
                .headers(Map.of("Account-Id", siteAccountId))
                .executeWithJsonResponse(new TypeReference<>() {});
        assertThat(maybeState).isPresent();
        VerificationState state = maybeState.get();
        assertThat(state.status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    /** Dagger components that provides an {@link HttpHandler}. */
    @Component(modules = TestSiteServiceModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createApiHandler() {
            TestComponent component =
                    DaggerSiteServiceEndpointTest_TestComponent.factory().create(fakeAvsServer);
            return component.apiHandler();
        }

        @Named("api")
        HttpHandler apiHandler();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("avs") TestServer<?> avsServer);
        }
    }

    /** Dagger components that provides an {@link HttpHandler}. */
    @Component(modules = TestFakeAvsServiceModule.class)
    @Singleton
    interface FakeAvsComponent {

        static HttpHandler createApiHandler() {
            FakeAvsComponent component =
                    DaggerSiteServiceEndpointTest_FakeAvsComponent.factory().create(siteServer);
            return component.apiHandler();
        }

        @Named("api")
        HttpHandler apiHandler();

        @Component.Factory
        interface Factory {

            FakeAvsComponent create(@BindsInstance @Named("site") TestServer<?> siteServer);
        }
    }
}
