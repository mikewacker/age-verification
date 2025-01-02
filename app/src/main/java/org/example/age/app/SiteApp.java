package org.example.age.app;

import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.SiteApi;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.crypto.SecureId;

/** Application for a site. */
public final class SiteApp extends NamedApp<Configuration> {

    /** Creates ands runs an application. */
    public static void run(String name, String appConfigPath) throws Exception {
        new SiteApp(name).runServer(appConfigPath);
    }

    /** Creates a named application. */
    public SiteApp(String name) {
        super(name);
    }

    /** Creates an application with the default name. Provided for the purpose of testing. */
    public SiteApp() {}

    @Override
    public void run(Configuration configuration, Environment environment) {
        environment.jersey().register(new StubSiteService());
    }

    /** Stub service implementation of {@link SiteApi}. */
    private static final class StubSiteService implements SiteApi {

        @Override
        public CompletionStage<VerificationState> getVerificationState() {
            VerificationState state = VerificationState.builder()
                    .status(VerificationStatus.UNVERIFIED)
                    .build();
            return CompletableFuture.completedFuture(state);
        }

        @Override
        public CompletionStage<VerificationRequest> createVerificationRequest() {
            VerificationRequest request = VerificationRequest.builder()
                    .id(SecureId.generate())
                    .siteId("site")
                    .expiration(OffsetDateTime.now(ZoneOffset.UTC))
                    .build();
            return CompletableFuture.completedFuture(request);
        }

        @Override
        public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
            return CompletableFuture.completedFuture(null);
        }
    }
}
