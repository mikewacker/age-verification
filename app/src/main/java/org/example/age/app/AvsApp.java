package org.example.age.app;

import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.AuthMatchData;
import org.example.age.api.AvsApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.crypto.SecureId;

/** Application for the age verification service. */
public class AvsApp extends NamedApp<Configuration> {

    /** Creates and runs an application. */
    public static void run(String name, String appConfigPath) throws Exception {
        new AvsApp(name).runServer(appConfigPath);
    }

    /** Creates a named application. */
    public AvsApp(String name) {
        super(name);
    }

    /** Creates an application with the default name. Provided for the purpose of testing. */
    public AvsApp() {}

    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(new StubAvsService());
    }

    /** Sub service implementation of {@link AvsApi}. */
    private static final class StubAvsService implements AvsApi {

        @Override
        public CompletionStage<VerificationRequest> createVerificationRequestForSite(
                String siteId, AuthMatchData authMatchData) {
            VerificationRequest request = VerificationRequest.builder()
                    .id(SecureId.generate())
                    .siteId("site")
                    .expiration(OffsetDateTime.now(ZoneOffset.UTC))
                    .build();
            return CompletableFuture.completedFuture(request);
        }

        @Override
        public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<Void> sendAgeCertificate() {
            return CompletableFuture.completedFuture(null);
        }
    }
}
