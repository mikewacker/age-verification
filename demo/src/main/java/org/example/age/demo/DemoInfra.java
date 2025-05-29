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
import org.example.age.api.AgeRange;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.SecureId;
import org.example.age.common.testing.JsonTesting;
import org.example.age.common.testing.TestClient;
import org.example.age.testing.containers.TestContainers;

/** Client/server infrastructure for the demo. */
public final class DemoInfra {

    private static final TestContainers containers = new TestContainers();
    private static final ObjectWriter objectWriter = Jackson.newObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .writerWithDefaultPrettyPrinter();

    /** Populates Redis with verified persons. */
    public static void populateRedis() throws IOException {
        containers.beforeAll(null);
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
        containers.redisClient().set("age:user:John Smith", JsonTesting.serialize(parent));
        containers.redisClient().set("age:user:Billy Smith", JsonTesting.serialize(child));
    }

    /** Starts an application. */
    public static void startServer(Application<?> app, String configPath) throws Exception {
        app.run("server", getResourcePath(configPath));
    }

    /** Creates a client for an account. */
    public static <S> S createClient(int port, String accountId, Class<S> serviceClass) {
        return TestClient.createApi(
                port, requestBuilder -> requestBuilder.addHeader("Account-Id", accountId), serviceClass);
    }

    /** Gets an {@link ObjectWriter} that pretty-prints JSON. */
    public static ObjectWriter getObjectWriter() {
        return objectWriter;
    }

    /** Stops everything (and terminates the application). */
    public static void stop() throws Exception {
        containers.afterAll(null);
        System.exit(0);
    }

    /** Get the absolute path of a resource file. */
    private static String getResourcePath(String relativePath) throws URISyntaxException {
        ClassLoader classLoader = DemoInfra.class.getClassLoader();
        URI fileUri = classLoader.getResource(relativePath).toURI();
        return new File(fileUri).getAbsolutePath();
    }

    // static class
    private DemoInfra() {}
}
