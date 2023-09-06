package org.example.age.adult.html;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class VerifyHtmlHttpHandlerTest {

    @RegisterExtension
    private static final TestServer server = TestServer.create(TestComponent::createHandler);

    @Test
    public void redirectUnverifiedUser() throws IOException {
        OkHttpClient client = TestClient.getInstance();
        Request request = new Request.Builder().url(server.getRootUrl()).build();
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(303);
        assertThat(response.header("Location")).isEqualTo("/verify.html");
    }

    @Test
    public void getVerifyHtml() throws IOException {
        OkHttpClient client = TestClient.getInstance();
        Request request =
                new Request.Builder().url(server.getUrl("/verify.html")).build();
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).isEqualTo("text/html");
        assertThat(response.body().string()).isNotEmpty();
    }

    @Test
    public void getFavIcon() throws IOException {
        OkHttpClient client = TestClient.getInstance();
        Request request =
                new Request.Builder().url(server.getUrl("/favicon.ico")).build();
        Response response = client.newCall(request).execute();
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
