package org.example.age.common.server.html;

import static org.assertj.core.api.Assertions.assertThat;

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

public final class HtmlTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.fromHandler(TestComponent::createHandler);

    @Test
    public void getHtmlFile() throws IOException {
        Response response = TestClient.get(server.rootUrl());
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).endsWith("text/html");
        assertThat(response.body().string()).isEqualTo("<p>test</p>");
    }

    /** Dagger module that binds dependencies for <code>@Named("html") {@link HttpHandler}</code>. */
    @Module(includes = HtmlModule.class)
    interface TestModule {

        @Provides
        @Named("html")
        @Singleton
        static Class<?> provideHtmlClass() {
            return HtmlTest.class;
        }
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerHtmlTest_TestComponent.create();
            return component.handler();
        }

        @Named("html")
        HttpHandler handler();
    }
}
