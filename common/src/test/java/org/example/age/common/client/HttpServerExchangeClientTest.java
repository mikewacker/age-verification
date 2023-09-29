package org.example.age.common.client;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import javax.inject.Singleton;
import okhttp3.Response;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class HttpServerExchangeClientTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.create(TestComponent::createHandler);

    @Test
    public void exchange() throws IOException {
        Response response = TestClient.get(server.rootUrl());
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).isEqualTo("text/plain");
        assertThat(response.body().string()).isEqualTo("Hello, world!");
    }

    /**
     * Dagger module that publishes a binding for {@link HttpHandler},
     * which uses an {@link HttpServerExchangeClient}.
     */
    @Module(includes = ClientModule.class)
    interface TestModule {

        @Binds
        HttpHandler bindHttpHandler(TestGreetingHandler impl);
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerHttpServerExchangeClientTest_TestComponent.create();
            return component.handler();
        }

        HttpHandler handler();
    }
}
