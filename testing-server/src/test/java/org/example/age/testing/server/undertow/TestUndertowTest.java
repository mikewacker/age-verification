package org.example.age.testing.server.undertow;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Response;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class TestUndertowTest {

    @RegisterExtension
    public static TestUndertowServer server = TestUndertowServer.create(TestComponent::createServer);

    @Test
    public void apiExchange() throws IOException {
        Response response = TestClient.get(server.url("/api/example"));
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("test");
    }

    @Test
    public void htmlExchange() throws IOException {
        Response response = TestClient.get(server.rootUrl());
        assertThat(response.code()).isEqualTo(404);
    }

    private static void handleApiRequest(HttpServerExchange exchange) {
        exchange.getResponseSender().send("test");
    }

    /**
     * Dagger module that binds dependencies needed to create an {@link Undertow}.
     *
     * <p>Depends on an unbound <code>@Named("port") int</code>.</p>
     */
    @Module(includes = TestUndertowModule.class)
    interface TestModule {

        @Provides
        @Named("api")
        @Singleton
        static HttpHandler provideApiHandler() {
            return TestUndertowTest::handleApiRequest;
        }
    }

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static Undertow createServer(int port) {
            TestComponent component =
                    DaggerTestUndertowTest_TestComponent.factory().create(port);
            return component.server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
