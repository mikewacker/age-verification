package org.example.age.resources;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
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

public final class HtmlHttpHandlerTest {

    @RegisterExtension
    private static final TestServer server = TestServer.create(HtmlHttpHandlerTest::createHandler);

    @Test
    public void getHtmlFile() throws IOException {
        OkHttpClient client = TestClient.getInstance();
        Request request = new Request.Builder().url(server.getRootUrl()).build();
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).endsWith("text/html");
        assertThat(response.body().string()).isEqualTo("<p>test</p>");
    }

    private static HttpHandler createHandler() {
        TestComponent component = TestComponent.create(HtmlHttpHandlerTest.class);
        return component.handler();
    }

    /** Component that provides a {@code @Named("html") HttpHandler}. */
    @Component(modules = HtmlModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create(Class<?> clazz) {
            return DaggerHtmlHttpHandlerTest_TestComponent.factory().create(clazz);
        }

        @Named("html")
        HttpHandler handler();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance @Named("html") Class<?> clazz);
        }
    }
}
