package org.example.age.module.client;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.age.api.AuthMatchData;
import org.example.age.api.VerificationRequest;
import org.example.age.api.client.AvsApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.module.client.testing.LazyPort;
import org.example.age.testing.TestPort;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

public final class SiteClientTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app = new DropwizardAppExtension<>(TestApp.class);

    private static final LazyPort port = new LazyPort();

    private static AvsApi avsClient;

    @BeforeAll
    public static void createClients() {
        TestComponent component = TestComponent.create();
        avsClient = component.avsClient();
    }

    @Test
    public void avsClient() throws IOException {
        Response<Void> response = avsClient.sendAgeCertificate().execute();
        assertThat(response.isSuccessful()).isTrue();
    }

    /** Stub service implementation of {@link org.example.age.api.AvsApi}. */
    public static final class StubAvsService implements org.example.age.api.AvsApi {

        @Override
        public CompletionStage<VerificationRequest> createVerificationRequestForSite(
                String siteId, AuthMatchData authMatchData) {
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

    /** Test application that runs {@link StubAvsService}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) throws Exception {
            TestPort.set(config, port.get());
            env.jersey().register(new StubAvsService());
        }
    }

    /** Dagger component for the clients. */
    @Component(modules = {SiteClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerSiteClientTest_TestComponent.create();
        }

        @Named("client")
        AvsApi avsClient();
    }

    /**
     * Dagger module that binds...
     * <ul>
     *     <li>{@link SiteClientsConfig}
     *     <li><code>@Named("worker") {@link ExecutorService}</code>
     * </ul>
     */
    @Module
    interface TestDependenciesModule {

        @Provides
        @Singleton
        static SiteClientsConfig provideSiteClientsConfig() {
            try {
                URL url = new URI(String.format("http://localhost:%d", port.get())).toURL();
                return SiteClientsConfig.builder().avsUrl(url).build();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Provides
        @Named("worker")
        @Singleton
        static ExecutorService providerWorker() {
            return Executors.newFixedThreadPool(1);
        }
    }
}
