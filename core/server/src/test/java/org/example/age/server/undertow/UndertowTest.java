package org.example.age.server.undertow;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.client.JsonApiClient;
import io.github.mikewacker.drift.testing.server.TestServer;
import io.github.mikewacker.drift.testing.server.TestUndertowServer;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class UndertowTest {

    @RegisterExtension
    private static final TestServer<?> apiServer =
            TestUndertowServer.register("api", ApiUndertowComponent::createServer);

    @RegisterExtension
    private static final TestServer<?> fullServer =
            TestUndertowServer.register("full", FullUndertowComponent::createServer);

    private static OkHttpClient htmlClient;

    @BeforeAll
    public static void createHtmlClient() {
        htmlClient = new OkHttpClient();
    }

    @Test
    public void apiServer_apiRequest() throws IOException {
        HttpOptional<String> maybeText = executeApiRequest("api");
        assertThat(maybeText).hasValue("api");
    }

    @Test
    public void apiServer_htmlRequest() throws IOException {
        HttpOptional<String> maybeHtml = executeHtmlRequest("api");
        assertThat(maybeHtml).isEmptyWithErrorCode(404);
    }

    @Test
    public void apiServer_ajaxRequest() throws IOException {
        HttpOptional<String> maybeText = executeAjaxRequest("api");
        assertThat(maybeText).isEmptyWithErrorCode(404);
    }

    @Test
    public void fullServer_ApiRequest() throws IOException {
        HttpOptional<String> maybeText = executeApiRequest("full");
        assertThat(maybeText).hasValue("api");
    }

    @Test
    public void fullServer_htmlRequest() throws IOException {
        HttpOptional<String> maybeHtml = executeHtmlRequest("full");
        assertThat(maybeHtml).hasValue("<p>test</p>");
    }

    @Test
    public void fullServer_ajaxRequest() throws IOException {
        HttpOptional<String> maybeText = executeAjaxRequest("full");
        assertThat(maybeText).hasValue("ajax");
    }

    private HttpOptional<String> executeApiRequest(String serverName) throws IOException {
        return JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<String>() {})
                .get(TestServer.get(serverName).url("/api/test"))
                .build()
                .execute();
    }

    private HttpOptional<String> executeHtmlRequest(String serverName) throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(TestServer.get(serverName).rootUrl())
                .build();
        Response response = htmlClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            return HttpOptional.empty(response.code());
        }

        assertThat(response.header("Content-Type")).isEqualTo("text/html");
        String html = response.body().string();
        return HttpOptional.of(html);
    }

    private HttpOptional<String> executeAjaxRequest(String serverName) throws IOException {
        return JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<String>() {})
                .get(TestServer.get(serverName).url("/ajax/test"))
                .build()
                .execute();
    }

    /** Stub HTTP handler for API requests. */
    private static void handleApiRequest(HttpServerExchange exchange) {
        sendJson(exchange, "\"api\"");
    }

    /** Stub HTTP handler for AJAX requests. */
    private static void handleAjaxRequest(HttpServerExchange exchange) {
        sendJson(exchange, "\"ajax\"");
    }

    private static void sendJson(HttpServerExchange exchange, String json) {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(json);
    }

    /**
     * Dagger module that binds dependencies for {@link Undertow}.
     *
     * <p>Depends on an unbound {@code @Named("port") int}.</p>
     */
    @Module(includes = UndertowModule.class)
    interface ApiUndertowModule {

        @Provides
        @Named("host")
        @Singleton
        static String provideHost() {
            return "localhost";
        }

        @Provides
        @Named("api")
        @Singleton
        static HttpHandler provideApiHandler() {
            return UndertowTest::handleApiRequest;
        }
    }

    /**
     * Dagger module that binds dependencies for {@link Undertow}.
     *
     * <p>Depends on an unbound {@code @Named("port") int}.</p>
     */
    @Module(includes = ApiUndertowModule.class)
    interface FullUndertowModule {

        @Provides
        @Named("html")
        @Singleton
        static Class<?> provideHtmlClass() {
            return UndertowTest.class;
        }

        @Provides
        @Named("ajax")
        @Singleton
        static HttpHandler provideAjaxHandler() {
            return UndertowTest::handleAjaxRequest;
        }
    }

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = ApiUndertowModule.class)
    @Singleton
    interface ApiUndertowComponent {

        static Undertow createServer(int port) {
            return DaggerUndertowTest_ApiUndertowComponent.factory()
                    .create(port)
                    .server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            ApiUndertowComponent create(@BindsInstance @Named("port") int port);
        }
    }

    /** Dagger component that provides an {@link Undertow} server. */
    @Component(modules = FullUndertowModule.class)
    @Singleton
    interface FullUndertowComponent {

        static Undertow createServer(int port) {
            return DaggerUndertowTest_FullUndertowComponent.factory()
                    .create(port)
                    .server();
        }

        Undertow server();

        @Component.Factory
        interface Factory {

            FullUndertowComponent create(@BindsInstance @Named("port") int port);
        }
    }
}
