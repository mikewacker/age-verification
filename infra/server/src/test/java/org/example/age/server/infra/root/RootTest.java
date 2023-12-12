package org.example.age.server.infra.root;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.IOException;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.base.HttpOptional;
import org.example.age.server.infra.html.HtmlModule;
import org.example.age.testing.api.HttpOptionalAssert;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.client.TestHtmlClient;
import org.example.age.testing.server.TestServer;
import org.example.age.testing.server.undertow.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RootTest {

    @RegisterExtension
    public static TestServer<?> server = TestUndertowServer.register("test", TestComponent::createHandler);

    @Test
    public void exchange_Api() throws IOException {
        HttpOptional<String> maybeValue = TestClient.requestBuilder(new TypeReference<String>() {})
                .get(server.url("/api/test"))
                .execute();
        assertThat(maybeValue).hasValue("test");
    }

    @Test
    public void exchange_Html() throws IOException {
        HttpOptional<String> maybeHtml = TestHtmlClient.get(server.rootUrl());
        HttpOptionalAssert.assertThat(maybeHtml).hasValue("<p>test</p>");
    }

    /** {@link HttpHandler} for API requests that sends a stub response. */
    private static void handleApiRequest(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("\"test\"");
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
