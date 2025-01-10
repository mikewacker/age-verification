package org.example.age.module.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.client.SiteApi;
import org.example.age.module.client.testing.LazyPort;
import org.example.age.module.client.testing.TestDependenciesModule;
import org.example.age.service.module.client.SiteClientRepository;
import org.example.age.testing.TestPort;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

public final class AvsClientTest {

    @RegisterExtension
    private static final DropwizardAppExtension<Configuration> app = new DropwizardAppExtension<>(TestApp.class);

    private static final LazyPort port = new LazyPort();

    private static SiteClientRepository siteClients;

    @BeforeAll
    public static void createClients() {
        TestComponent component = TestComponent.create();
        siteClients = component.siteClients();
    }

    @Test
    public void siteClient() throws IOException {
        SiteApi siteClient = siteClients.get("site");
        Response<VerificationState> response = siteClient.getVerificationState().execute();
        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    public void error_UnregisteredSite() {
        assertThatThrownBy(() -> siteClients.get("unregistered-site")).isInstanceOf(NotFoundException.class);
    }

    /** Stub service implementation of {@link org.example.age.api.SiteApi}. */
    public static final class StubSiteService implements org.example.age.api.SiteApi {

        @Override
        public CompletionStage<VerificationState> getVerificationState() {
            VerificationState state = VerificationState.builder()
                    .status(VerificationStatus.UNVERIFIED)
                    .build();
            return CompletableFuture.completedFuture(state);
        }

        @Override
        public CompletionStage<VerificationRequest> createVerificationRequest() {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }

        @Override
        public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
            return CompletableFuture.failedFuture(new UnsupportedOperationException());
        }
    }

    /** Test application that runs {@link StubSiteService}. */
    public static final class TestApp extends Application<Configuration> {

        @Override
        public void run(Configuration config, Environment env) {
            TestPort.set(config, port.get());
            env.jersey().register(new StubSiteService());
        }
    }

    /** Dagger component for the clients. */
    @Component(modules = {AvsClientModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerAvsClientTest_TestComponent.factory().create(port);
        }

        SiteClientRepository siteClients();

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance LazyPort port);
        }
    }
}
