package org.example.age.adult.html;

import static org.assertj.core.api.Assertions.assertThat;

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

public final class VerifyHtmlHttpHandlerTest {

    @RegisterExtension
    private static final TestUndertowServer server = TestUndertowServer.create(TestComponent::createHandler);

    @Test
    public void redirectUnverifiedUser() throws IOException {
        Response response = TestClient.get(server.rootUrl());
        assertThat(response.code()).isEqualTo(303);
        assertThat(response.header("Location")).isEqualTo("/verify.html");
    }

    @Test
    public void getVerifyHtml() throws IOException {
        Response response = TestClient.get(server.url("/verify.html"));
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).isEqualTo("text/html");
        assertThat(response.body().string()).isNotEmpty();
    }

    @Test
    public void getFavIcon() throws IOException {
        Response response = TestClient.get(server.url("/favicon.ico"));
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).isEqualTo("image/x-icon");
        assertThat(response.body().bytes()).isNotEmpty();
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = VerifyHtmlModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerVerifyHtmlHttpHandlerTest_TestComponent.create();
            return component.handler();
        }

        @Named("verifyHtml")
        HttpHandler handler();
    }
}
