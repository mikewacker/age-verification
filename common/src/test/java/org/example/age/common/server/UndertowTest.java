package org.example.age.common.server;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.net.HostAndPort;
import dagger.Binds;
import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Response;
import org.example.age.common.html.HtmlModule;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowTest {

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
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("<p>test</p>");
    }

    private static void handleApiRequest(HttpServerExchange exchange) {
        exchange.getResponseSender().send("test");
    }

    /**
     * Dagger module that binds dependencies needed to create an {@link Undertow}.
     *
     * <p>Depends on an unbound <code>@Named("port") int</code>.</p>
     */
    @Module(includes = {UndertowModule.class, HtmlModule.class})
    interface TestModule {

        @Provides
        @Named("api")
        @Singleton
        static HttpHandler provideApiHttpHandler() {
            return UndertowTest::handleApiRequest;
        }

        @Binds
        @Named("verifyHtml")
        HttpHandler bindVerifyHtmlHttpHandler(@Named("html") HttpHandler delegate);

        @Provides
        @Named("html")
        @Singleton
        static Class<?> provideHtmlClass() {
            return UndertowTest.class;
        }

        @Provides
        @Singleton
        static Supplier<HostAndPort> provideHostAndPort(@Named("port") int port) {
            return () -> HostAndPort.fromParts("localhost", port);
        }
    }

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static Undertow createServer(int port) {
            TestComponent component = DaggerUndertowTest_TestComponent.factory().create(port);
            return component.server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
