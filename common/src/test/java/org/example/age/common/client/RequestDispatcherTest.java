package org.example.age.common.client;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.net.URL;
import java.util.function.Supplier;
import javax.inject.Singleton;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RequestDispatcherTest {

    @RegisterExtension
    private static final TestServer frontendServer = TestServer.create(TestComponent::createHandler);

    private static MockWebServer stubBackendServer;

    @BeforeEach
    public void startBackedServer() throws IOException {
        stubBackendServer = new MockWebServer();
        stubBackendServer.start();
    }

    @AfterEach
    public void stopBackendServer() throws IOException {
        stubBackendServer.shutdown();
        stubBackendServer = null;
    }

    @Test
    public void exchange() throws IOException {
        stubBackendServer.enqueue(new MockResponse().setBody("test"));
        Response response = TestClient.get(frontendServer.getRootUrl());
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("test");
    }

    @Test
    public void backendError_4xx() throws IOException {
        stubBackendServer.enqueue(new MockResponse().setResponseCode(400));
        Response response = TestClient.get(frontendServer.getRootUrl());
        assertThat(response.code()).isEqualTo(500);
    }

    @Test
    public void backendError_5xx() throws IOException {
        stubBackendServer.enqueue(new MockResponse().setResponseCode(500));
        Response response = TestClient.get(frontendServer.getRootUrl());
        assertThat(response.code()).isEqualTo(502);
    }

    @Test
    public void backendError_DisconnectAfterRequest() throws IOException {
        stubBackendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
        Response response = TestClient.get(frontendServer.getRootUrl());
        assertThat(response.code()).isEqualTo(502);
    }

    @Test
    public void backendError_DisconnectDuringResponseBody() throws IOException {
        stubBackendServer.enqueue(
                new MockResponse().setBody("test").setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));
        Response response = TestClient.get(frontendServer.getRootUrl());
        assertThat(response.code()).isEqualTo(502);
    }

    @Test
    public void frontendError_CallbackException() throws IOException {
        stubBackendServer.enqueue(new MockResponse().setBody("error")); // special exception trigger
        Response response = TestClient.get(frontendServer.getRootUrl());
        assertThat(response.code()).isEqualTo(500);
    }

    /** Dagger module that publishes a binding for {@link HttpHandler}, which uses a {@link RequestDispatcher}. */
    @Module(includes = RequestModule.class)
    interface TestModule {

        @Binds
        HttpHandler bindHttpHandler(TestProxyHandler impl);

        @Provides
        @Singleton
        static Supplier<URL> provideUrlSupplier() {
            return () -> stubBackendServer.url("").url();
        }
    }

    /** Dagger component that provides an {@link HttpHandler}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static HttpHandler createHandler() {
            TestComponent component = DaggerRequestDispatcherTest_TestComponent.create();
            return component.handler();
        }

        HttpHandler handler();
    }
}
