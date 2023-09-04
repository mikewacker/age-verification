package org.example.age.common.client;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import javax.inject.Singleton;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class HttpServerExchangeClientTest {

    @RegisterExtension
    private static final TestServer server = TestServer.create(TestComponent::createHandler);

    @Test
    public void exchange() throws IOException {
        OkHttpClient client = TestClient.getInstance();
        Request request = new Request.Builder().url(server.getRootUrl()).build();
        Response response = client.newCall(request).execute();
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.header("Content-Type")).isEqualTo("text/plain");
        assertThat(response.body().string()).isEqualTo("Hello, world!");
    }

    /**
     * Dagger module that publishes a binding for {@link HttpHandler},
     * which uses an {@link HttpServerExchangeClient}.
     */
    @Module(includes = ClientModule.class)
    interface TestModule {

        @Binds
        HttpHandler bindHttpHandler(TestGreetingHandler impl);
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerHttpServerExchangeClientTest_TestComponent.create();
            return component.handler();
        }

        HttpHandler handler();
    }
}
