package org.example.age.app;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.Configuration;
import io.dropwizard.core.setup.Environment;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.Duration;
import org.example.age.api.SiteApi;
import org.example.age.api.client.AvsApi;
import org.example.age.module.crypto.test.TestSiteCryptoModule;
import org.example.age.module.request.test.TestAccountId;
import org.example.age.module.request.test.TestRequestModule;
import org.example.age.module.store.test.TestPendingStoreModule;
import org.example.age.module.store.test.TestSiteAccountStoreModule;
import org.example.age.service.SiteServiceConfig;
import org.example.age.service.SiteServiceModule;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/** Test application for the site. */
public final class TestSiteApp extends Application<Configuration> {

    @Override
    public void run(Configuration config, Environment env) {
        AppComponent component = createAppComponent(env);
        env.jersey().register(component.service());
        component.accountId().set("username");
    }

    /** Creates the component for the application. */
    private static AppComponent createAppComponent(Environment env) {
        AvsApi avsClient = createAvsClient(env);
        SiteServiceConfig serviceConfig = createServiceConfig();
        return AppComponent.create(avsClient, serviceConfig);
    }

    /** Creates the client for the age verification service. */
    private static AvsApi createAvsClient(Environment env) {
        return new Retrofit.Builder()
                .baseUrl("http://localhost:9090")
                .addConverterFactory(JacksonConverterFactory.create(env.getObjectMapper()))
                .build()
                .create(AvsApi.class);
    }

    /** Creates the configuration for the service. */
    private static SiteServiceConfig createServiceConfig() {
        return SiteServiceConfig.builder()
                .id("site1")
                .verifiedAccountExpiresIn(Duration.ofDays(30))
                .build();
    }

    /** Dagger component for the application. */
    @Component(
            modules = {
                SiteServiceModule.class,
                TestRequestModule.class,
                TestSiteAccountStoreModule.class,
                TestPendingStoreModule.class,
                TestSiteCryptoModule.class,
            })
    @Singleton
    interface AppComponent {

        static AppComponent create(AvsApi avsClient, SiteServiceConfig serviceConfig) {
            return DaggerTestSiteApp_AppComponent.factory().create(avsClient, serviceConfig);
        }

        @Named("service")
        SiteApi service();

        TestAccountId accountId();

        @Component.Factory
        interface Factory {

            AppComponent create(
                    @BindsInstance @Named("client") AvsApi avsClient, @BindsInstance SiteServiceConfig serviceConfig);
        }
    }
}
