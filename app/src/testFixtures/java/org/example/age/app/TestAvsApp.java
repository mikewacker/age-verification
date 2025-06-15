package org.example.age.app;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.util.Map;
import org.example.age.api.AgeThresholds;
import org.example.age.api.AvsApi;
import org.example.age.api.client.SiteApi;
import org.example.age.module.crypto.test.TestAvsCryptoModule;
import org.example.age.module.request.test.TestAccountId;
import org.example.age.module.request.test.TestRequestModule;
import org.example.age.module.store.test.TestAvsAccountStoreModule;
import org.example.age.module.store.test.TestPendingStoreModule;
import org.example.age.service.AvsServiceConfig;
import org.example.age.service.AvsServiceModule;
import org.example.age.service.module.client.SiteClientRepository;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/** Test application for the age verification service. */
public final class TestAvsApp extends Application<Configuration> {

    @Override
    public void run(Configuration config, Environment env) {
        AppComponent component = createAppComponent(env);
        env.jersey().register(component.service());
        component.accountId().set("person");
    }

    /** Creates the component for the application. */
    private static AppComponent createAppComponent(Environment env) {
        SiteApi siteClient = createSiteClient(env);
        Map<String, SiteApi> siteClients = Map.of("site1", siteClient);
        AvsServiceConfig serviceConfig = createServiceConfig();
        return AppComponent.create(siteClients::get, serviceConfig);
    }

    /** Creates the client for the site. */
    private static SiteApi createSiteClient(Environment env) {
        return new Retrofit.Builder()
                .baseUrl("http://localhost:8080")
                .addConverterFactory(JacksonConverterFactory.create(env.getObjectMapper()))
                .build()
                .create(SiteApi.class);
    }

    /** Creates the configuration for the service. */
    private static AvsServiceConfig createServiceConfig() {
        return AvsServiceConfig.builder()
                .verificationRequestExpiresIn(Duration.ofMinutes(5))
                .putAgeThresholds("site1", AgeThresholds.of(18))
                .build();
    }

    /** Dagger component for the application. */
    @Component(
            modules = {
                AvsServiceModule.class,
                TestRequestModule.class,
                TestAvsAccountStoreModule.class,
                TestPendingStoreModule.class,
                TestAvsCryptoModule.class,
            })
    @Singleton
    interface AppComponent {

        static AppComponent create(SiteClientRepository siteClients, AvsServiceConfig serviceConfig) {
            return DaggerTestAvsApp_AppComponent.factory().create(siteClients, serviceConfig);
        }

        @Named("service")
        AvsApi service();

        TestAccountId accountId();

        @Component.Factory
        interface Factory {

            AppComponent create(
                    @BindsInstance SiteClientRepository siteClients, @BindsInstance AvsServiceConfig serviceConfig);
        }
    }
}
