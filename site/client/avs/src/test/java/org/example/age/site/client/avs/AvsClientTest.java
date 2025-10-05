package org.example.age.site.client.avs;

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.env.TestEnvModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

public final class AvsClientTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app =
            new DropwizardAppExtension<>(TestApp.class, null, ConfigOverride.randomPorts());

    @Test
    public void useClient() throws IOException {
        AvsApi client = TestComponent.create();
        Response<Void> response = client.sendAgeCertificate().execute();
        assertThat(response.isSuccessful()).isTrue();
    }

    /** Stub endpoint for {@link org.example.age.avs.api.AvsApi}. */
    public static final class StubAvsEndpoint implements org.example.age.avs.api.AvsApi {

        @Override
        public CompletionStage<VerificationRequest> createVerificationRequestForSite(String siteId) {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }

        @Override
        public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }

        @Override
        public CompletionStage<Void> sendAgeCertificate() {
            return CompletableFuture.completedFuture(null);
        }
    }

    /** Test application for {@link StubAvsEndpoint}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            env.jersey().register(new StubAvsEndpoint());
        }
    }

    /** Dagger component for {@link AvsApi}. */
    @Component(modules = {AvsClientModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<AvsApi> {

        static AvsApi create() {
            AvsClientConfig config = AvsClientConfig.builder()
                    .url(TestClient.localhostUrl(app.getLocalPort()))
                    .build();
            return DaggerAvsClientTest_TestComponent.factory().create(config).get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance AvsClientConfig config);
        }
    }
}
