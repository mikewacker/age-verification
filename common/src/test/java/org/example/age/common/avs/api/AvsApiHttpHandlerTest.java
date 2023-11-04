package org.example.age.common.avs.api;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.security.KeyPair;
import java.time.Duration;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.common.avs.config.AvsConfig;
import org.example.age.common.avs.store.InMemoryRegisteredSiteConfigStoreModule;
import org.example.age.common.avs.store.InMemoryVerifiedUserStoreModule;
import org.example.age.common.base.auth.UserAgentAuthMatchDataExtractorModule;
import org.example.age.common.base.store.InMemoryPendingStoreFactoryModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.crypto.TestKeys;
import org.example.age.testing.server.TestUndertowServer;
import org.example.age.testing.server.undertow.TestUndertowModule;
import org.example.age.testing.service.data.account.TestAccountIdExtractorModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class AvsApiHttpHandlerTest {

    @RegisterExtension
    private static final TestUndertowServer avsServer = TestUndertowServer.create(TestComponent::createServer);

    private static KeyPair avsSigningKeyPair;

    @BeforeAll
    public static void generateKeys() {
        avsSigningKeyPair = TestKeys.generateEd25519KeyPair();
    }

    @Test
    public void stubTest() throws IOException {
        String url = avsServer.url("/api/example");
        Request request = createPostRequest(url);
        Response response = TestClient.execute(request);
        assertThat(response.code()).isEqualTo(404);
    }

    private static Request createPostRequest(String url) {
        RequestBody emptyBody = RequestBody.create(new byte[0]);
        return new Request.Builder().url(url).post(emptyBody).build();
    }

    private static AvsConfig createAvsConfig() {
        return AvsConfig.builder()
                .privateSigningKey(avsSigningKeyPair.getPrivate())
                .expiresIn(Duration.ofMinutes(5))
                .build();
    }

    /** Dagger module that binds dependencies needed to create a <code>@Named("api") {@link HttpHandler}</code>. */
    @Module(
            includes = {
                AvsApiModule.class,
                TestAccountIdExtractorModule.class,
                UserAgentAuthMatchDataExtractorModule.class,
                InMemoryVerifiedUserStoreModule.class,
                InMemoryRegisteredSiteConfigStoreModule.class,
                InMemoryPendingStoreFactoryModule.class,
            })
    interface TestModule {

        @Provides
        @Singleton
        static AvsConfig provideAvsConfig() {
            return createAvsConfig();
        }
    }

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = {TestUndertowModule.class, TestModule.class})
    @Singleton
    interface TestComponent {

        static Undertow createServer(int port) {
            TestComponent component =
                    DaggerAvsApiHttpHandlerTest_TestComponent.factory().create(port);
            return component.server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
