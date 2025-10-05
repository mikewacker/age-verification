package org.example.age.avs.app;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.app.config.AvsAppConfig;
import org.example.age.avs.app.config.AvsConfigModule;
import org.example.age.avs.client.site.SiteClientsModule;
import org.example.age.avs.provider.accountstore.dynamodb.DynamoDbAvsAccountStoreModule;
import org.example.age.common.app.env.DropwizardEnvModule;
import org.example.age.common.provider.account.demo.DemoAccountIdModule;
import org.example.age.common.provider.pendingstore.redis.RedisPendingStoreModule;
import org.example.age.module.crypto.demo.DemoAvsCryptoModule;
import org.example.age.service.AvsServiceModule;

/** Application for the age verification service. */
public class AvsApp extends Application<AvsAppConfig> {

    @Override
    public String getName() {
        return "avs";
    }

    @Override
    public void run(AvsAppConfig appConfig, Environment env) {
        AppComponent component = AppComponent.create(appConfig, env);
        env.jersey().register(component.service());
    }

    /** Dagger component for the application. */
    @Component(
            modules = {
                AvsServiceModule.class,
                DemoAccountIdModule.class,
                SiteClientsModule.class,
                DynamoDbAvsAccountStoreModule.class,
                RedisPendingStoreModule.class,
                DemoAvsCryptoModule.class,
                AvsConfigModule.class,
                DropwizardEnvModule.class,
            })
    @Singleton
    interface AppComponent {

        static AppComponent create(AvsAppConfig appConfig, Environment env) {
            return DaggerAvsApp_AppComponent.factory().create(appConfig, env);
        }

        @Named("service")
        AvsApi service();

        @Component.Factory
        interface Factory {

            AppComponent create(@BindsInstance AvsAppConfig appConfig, @BindsInstance Environment env);
        }
    }
}
