package org.example.age.common.server.root;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Response;
import org.example.age.common.server.html.HtmlModule;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RootTest {

    @RegisterExtension
    public static TestUndertowServer server = TestUndertowServer.fromHandler(TestComponent::createHandler);

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

    /** Dagger module that binds dependencies for {@link HttpHandler}. */
    @Module(includes = {RootModule.class, HtmlModule.class})
    interface TestModule {

        @Provides
        @Named("api")
        @Singleton
        static HttpHandler provideApiHandler() {
            return RootTest::handleApiRequest;
        }

        @Binds
        @Named("dynamicHtml")
        HttpHandler bindDynamicHtmlHandler(@Named("html") HttpHandler delegate);

        @Provides
        @Named("html")
        @Singleton
        static Class<?> provideHtmlClass() {
            return RootTest.class;
        }
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerRootTest_TestComponent.create();
            return component.handler();
        }

        HttpHandler handler();
    }
}
