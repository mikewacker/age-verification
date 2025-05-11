package org.example.age.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.dropwizard.core.Application;
import io.dropwizard.jackson.Jackson;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.example.age.api.AgeRange;
import org.example.age.api.VerifiedUser;
import org.example.age.api.client.retrofit.ApiClient;
import org.example.age.api.crypto.SecureId;
import org.example.age.testing.RedisExtension;
import org.example.age.testing.TestObjectMapper;

/** Client/server infrastructure for the demo. */
public final class DemoInfra {

    private static final RedisExtension redis = new RedisExtension(6379); // can safely share between apps
    private static final OkHttpClient httpClient = new OkHttpClient();
    private static final ObjectWriter objectWriter = Jackson.newObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .writerWithDefaultPrettyPrinter();

    /** Starts Redis. */
    public static void startRedis() throws Exception {
        redis.beforeAll(null);
    }

    /** Populates Redis with verified persons. */
    public static void populateRedis() throws IOException {
        SecureId parentPseudonym = SecureId.fromString("uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4");
        SecureId childPseudonym = SecureId.fromString("KB0b9pDo8j7-1p90fFokbgHj8hzbbU7jCGGjfuMzLR4");
        VerifiedUser parent = VerifiedUser.builder()
                .pseudonym(parentPseudonym)
                .ageRange(AgeRange.builder().min(40).max(40).build())
                .build();
        VerifiedUser child = VerifiedUser.builder()
                .pseudonym(childPseudonym)
                .ageRange(AgeRange.builder().min(13).max(13).build())
                .guardianPseudonyms(List.of(parentPseudonym))
                .build();
        String parentJson = TestObjectMapper.get().writeValueAsString(parent);
        redis.client().set("age:user:John Smith", parentJson);
        String childJson = TestObjectMapper.get().writeValueAsString(child);
        redis.client().set("age:user:Billy Smith", childJson);
    }

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

    /** Gets an {@link ObjectWriter} that pretty-prints JSON. */
    public static ObjectWriter getObjectWriter() {
        return objectWriter;
    }

    /** Stops everything (and terminates the application). */
    public static void stop() throws Exception {
        redis.afterAll(null);
        System.exit(0);
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
