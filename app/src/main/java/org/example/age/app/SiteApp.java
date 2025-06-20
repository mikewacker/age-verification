package org.example.age.app;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.api.SiteApi;
import org.example.age.app.config.SiteAppConfig;
import org.example.age.app.config.SiteConfigModule;
import org.example.age.app.env.EnvModule;
import org.example.age.module.client.SiteClientModule;
import org.example.age.module.crypto.demo.DemoSiteCryptoModule;
import org.example.age.module.request.demo.DemoAccountIdModule;
import org.example.age.module.store.dynamodb.DynamoDbSiteAccountStoreModule;
import org.example.age.module.store.redis.RedisPendingStoreModule;
import org.example.age.service.SiteServiceModule;

/** Application for a site. */
public final class SiteApp extends Application<SiteAppConfig> {

    private final String name;

    /** Creates an application. */
    public SiteApp(String name) {
        this.name = name;
    }

    /** Creates an application with the default name. */
    public SiteApp() {
        this("site");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void run(SiteAppConfig appConfig, Environment env) {
        AppComponent component = AppComponent.create(appConfig, env);
        env.jersey().register(component.service());
    }

    /** Dagger component for the application. */
    @Component(
            modules = {
                SiteServiceModule.class,
                DemoAccountIdModule.class,
                SiteClientModule.class,
                DynamoDbSiteAccountStoreModule.class,
                RedisPendingStoreModule.class,
                DemoSiteCryptoModule.class,
                SiteConfigModule.class,
                EnvModule.class,
            })
    @Singleton
    interface AppComponent {

        static AppComponent create(SiteAppConfig appConfig, Environment env) {
            return DaggerSiteApp_AppComponent.factory().create(appConfig, env);
        }

        @Named("service")
        SiteApi service();

        @Component.Factory
        interface Factory {

            AppComponent create(@BindsInstance SiteAppConfig appConfig, @BindsInstance Environment env);
        }
    }
}
