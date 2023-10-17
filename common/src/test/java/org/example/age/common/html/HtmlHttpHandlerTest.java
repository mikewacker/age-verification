package org.example.age.common.html;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Response;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestUndertowServer;
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

    /** Dagger component that provides a <code>@Named("html") {@link HttpHandler}</code>. */
    @Component(modules = HtmlModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            Class<?> clazz = HtmlHttpHandlerTest.class;
            TestComponent component =
                    DaggerHtmlHttpHandlerTest_TestComponent.factory().create(clazz);
            return component.handler();
        }

        @Named("html")
        HttpHandler handler();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("html") Class<?> clazz);
        }
    }
}
