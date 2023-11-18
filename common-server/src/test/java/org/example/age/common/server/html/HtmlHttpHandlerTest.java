package org.example.age.common.server.html;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Response;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.server.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class HtmlHttpHandlerTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.create(TestComponent::createHandler);

    @Test
    public void getHtmlFile() throws IOException {
        Response response = TestClient.get(server.rootUrl());
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).endsWith("text/html");
        assertThat(response.body().string()).isEqualTo("<p>test</p>");
    }

    /**
     * Dagger module that publishes a binding for {@link HttpHandler},
     * which delegates to <code>@Named("html") {@link HttpHandler}</code>.
     *
     * <p>Also binds dependencies for <code>@Named("html") {@link HttpHandler}</code>.</p>
     */
    @Module(includes = HtmlModule.class)
    interface TestModule {

        @Binds
        HttpHandler bindHandler(@Named("html") HttpHandler delegate);

        @Provides
        @Named("html")
        @Singleton
        static Class<?> provideHtmlClass() {
            return HtmlHttpHandlerTest.class;
        }
    }

    /** Dagger component that provides the root {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent extends TestUndertowServer.HandlerComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerHtmlHttpHandlerTest_TestComponent.create();
            return component.handler();
        }
    }
}
