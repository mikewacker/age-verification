package org.example.age.common.base.client.internal;

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
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.SocketPolicy;
import org.example.age.common.base.client.testing.TestTextProxyHandler;
import org.example.age.testing.MockServer;
import org.example.age.testing.TestClient;
import org.example.age.testing.TestUndertowServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class RequestDispatcherTest {

    @RegisterExtension
    private static final TestUndertowServer frontendServer = TestUndertowServer.create(TestComponent::createHandler);

    @RegisterExtension
    private static final MockServer backendServer = MockServer.create();

    @Test
    public void exchange_Ok() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("test"));
        Response response = TestClient.get(frontendServer.rootUrl());
        assertThat(response.code()).isEqualTo(200);
        assertThat(response.body().string()).isEqualTo("test");
    }

    @Test
    public void exchange_ErrorCode() throws IOException {
        backendServer.enqueue(new MockResponse().setResponseCode(400));
        Response response = TestClient.get(frontendServer.rootUrl());
        assertThat(response.code()).isEqualTo(400);
    }

    @Test
    public void backendFailure_ResponseFailure() throws IOException {
        backendServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AFTER_REQUEST));
        Response response = TestClient.get(frontendServer.rootUrl());
        assertThat(response.code()).isEqualTo(502);
    }

    @Test
    public void backendFailure_ResponseBodyFailure() throws IOException {
        backendServer.enqueue(
                new MockResponse().setBody("test").setSocketPolicy(SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY));
        Response response = TestClient.get(frontendServer.rootUrl());
        assertThat(response.code()).isEqualTo(502);
    }

    @Test
    public void backendFailure_BadResponseBody() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("deserialize error"));
        Response response = TestClient.get(frontendServer.rootUrl());
        assertThat(response.code()).isEqualTo(502);
    }

    @Test
    public void frontendCallbackException() throws IOException {
        backendServer.enqueue(new MockResponse().setBody("callback error"));
        Response response = TestClient.get(frontendServer.rootUrl());
        assertThat(response.code()).isEqualTo(500);
    }

    /** Dagger module that publishes a binding for {@link HttpHandler}, which uses a {@link RequestDispatcher}. */
    @Module(includes = RequestDispatcherModule.class)
    interface TestModule {

        @Binds
        HttpHandler bindHttpHandler(TestTextProxyHandler impl);

        @Provides
        @Named("backendUrl")
        static String provideBackendUrl() {
            return backendServer.rootUrl();
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
