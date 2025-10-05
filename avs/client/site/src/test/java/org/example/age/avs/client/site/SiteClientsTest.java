package org.example.age.avs.client.site;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.client.SiteApi;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.env.TestEnvModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

public final class SiteClientsTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    @Test
    public void useClient() throws IOException {
        SiteApi client = TestComponent.create().get("site");
        Response<VerificationRequest> response =
                client.createVerificationRequest().execute();
        assertThat(response.isSuccessful()).isTrue();
    }

    /** Stub endpoint for {@link org.example.age.site.api.SiteApi}. */
    public static final class StubSiteEndpoint implements org.example.age.site.api.SiteApi {

        @Override
        public CompletionStage<VerificationState> getVerificationState() {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }

        @Override
        public CompletionStage<VerificationRequest> createVerificationRequest() {
            VerificationRequest request = TestModels.createVerificationRequest("site");
            return CompletableFuture.completedFuture(request);
        }

        @Override
        public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }
    }

    /** Test application for {@link StubSiteEndpoint}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            env.jersey().register(new StubSiteEndpoint());
        }
    }

    /** Dagger component for <code>Map&lt;String, {@link SiteApi}&gt;</code>. */
    @Component(modules = {SiteClientsModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<Map<String, SiteApi>> {

        static Map<String, SiteApi> create() {
            SiteClientsConfig config = SiteClientsConfig.builder()
                    .putUrls("site", TestClient.localhostUrl(app.getLocalPort()))
                    .build();
            return DaggerSiteClientsTest_TestComponent.factory().create(config).get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance SiteClientsConfig config);
        }
    }
}
