package org.example.age.site.service.endpoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.util.Map;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.avs.service.endpoint.test.FakeAvsServiceModule;
import org.example.age.common.api.extractor.builtin.DisabledAuthMatchDataExtractorModule;
import org.example.age.common.api.extractor.test.TestAccountIdExtractorModule;
import org.example.age.common.service.key.test.TestKeyModule;
import org.example.age.common.service.store.inmemory.InMemoryPendingStoreFactoryModule;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.site.service.config.test.TestSiteConfigModule;
import org.example.age.site.service.store.InMemoryVerificationStoreModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class SiteServiceTest {

    @RegisterExtension
    private static final TestUndertowServer siteServer =
            TestUndertowServer.fromHandlerAtPath(TestComponent::createApiHandler, "/api/");

    @RegisterExtension
    private static final TestUndertowServer fakeAvsServer =
            TestUndertowServer.fromHandlerAtPath(FakeAvsComponent::createApiHandler, "/api/");

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
    }

    /** Dagger module that binds dependencies for <code>@Named("api") {@link HttpHandler}</code>. */
    @Module(
            includes = {
                SiteServiceModule.class,
                TestAccountIdExtractorModule.class,
                DisabledAuthMatchDataExtractorModule.class,
                InMemoryVerificationStoreModule.class,
                InMemoryPendingStoreFactoryModule.class,
                TestKeyModule.class,
                TestSiteConfigModule.class,
            })
    interface TestModule {}

    /** Dagger components that provides an {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createApiHandler() {
            TestComponent component =
                    DaggerSiteServiceTest_TestComponent.factory().create(fakeAvsServer);
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
    @Component(modules = FakeAvsServiceModule.class)
    @Singleton
    interface FakeAvsComponent {

        static HttpHandler createApiHandler() {
            FakeAvsComponent component =
                    DaggerSiteServiceTest_FakeAvsComponent.factory().create(siteServer);
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
