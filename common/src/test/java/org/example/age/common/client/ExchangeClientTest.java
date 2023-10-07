package org.example.age.common.client;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import org.example.age.testing.MockServer;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class ExchangeClientTest {

    @RegisterExtension
    private static final TestUndertowServer frontendServer = TestUndertowServer.create(TestComponent::createHandler);

    @RegisterExtension
    private static final MockServer backendServer = MockServer.create();

    @Test
    public void exchange() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("world"));
        Response response = TestClient.get(frontendServer.rootUrl());
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("Hello, world!");
    }

    /** Dagger module that publishes a binding for {@link HttpHandler}, which uses an {@link ExchangeClient}. */
    @Module(includes = ExchangeClientModule.class)
    interface TestModule {

        @Binds
        HttpHandler bindHttpHandler(TestGreetingHandler impl);

        @Provides
        @Named("backendUrl")
        @Singleton
        static Supplier<String> provideBackendUrl() {
            return () -> backendServer.rootUrl();
        }
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerExchangeClientTest_TestComponent.create();
            return component.handler();
        }

        HttpHandler handler();
    }
}
