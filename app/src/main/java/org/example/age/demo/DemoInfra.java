package org.example.age.demo;

import io.dropwizard.core.Application;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.age.api.client.retrofit.ApiClient;

/** Client/server infrastructure for the demo. */
public final class DemoInfra {

    private static final OkHttpClient httpClient = new OkHttpClient();

    /** Starts an application. */
    public static void startServer(Application<?> app, String configPath) throws Exception {
        app.run("server", getResourcePath(configPath));
    }

    /** Creates a client for an account. */
    public static <S> S createClient(int port, String accountId, Class<S> serviceClass) {
        OkHttpClient accountHttpClient = httpClient
                .newBuilder()
                .addInterceptor(chain -> chain.proceed(addAccountId(chain.request(), accountId)))
                .build();
        ApiClient apiClient = new ApiClient(accountHttpClient);
        String baseUrl = String.format("http://localhost:%d", port);
        apiClient.getAdapterBuilder().baseUrl(baseUrl);
        return apiClient.createService(serviceClass);
    }

    /** Get the absolute path of a resource file. */
    private static String getResourcePath(String relativePath) throws URISyntaxException {
        ClassLoader classLoader = DemoInfra.class.getClassLoader();
        URI fileUri = classLoader.getResource(relativePath).toURI();
        return new File(fileUri).getAbsolutePath();
    }

    /** Adds the account ID to the request. */
    private static Request addAccountId(Request request, String accountId) {
        return request.newBuilder().header("Account-Id", accountId).build();
    }

    // static class
    private DemoInfra() {}
}
