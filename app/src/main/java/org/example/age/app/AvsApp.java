package org.example.age.app;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.app.config.AvsAppConfig;
import org.example.age.app.config.AvsConfigModule;
import org.example.age.avs.api.AvsApi;
import org.example.age.common.env.LiteEnvModule;
import org.example.age.module.client.AvsClientModule;
import org.example.age.module.crypto.demo.DemoAvsCryptoModule;
import org.example.age.module.request.demo.DemoAccountIdModule;
import org.example.age.module.store.dynamodb.DynamoDbAvsAccountStoreModule;
import org.example.age.module.store.redis.RedisPendingStoreModule;
import org.example.age.service.AvsServiceModule;

/** Application for the age verification service. */
public class AvsApp extends Application<AvsAppConfig> {

    private final String name;

    /** Creates an application. */
    public AvsApp(String name) {
        this.name = name;
    }

    /** Creates an application with the default name. */
    public AvsApp() {
        this("avs");
    }

    @Override
    public String getName() {
        return name;
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
                AvsClientModule.class,
                DynamoDbAvsAccountStoreModule.class,
                RedisPendingStoreModule.class,
                DemoAvsCryptoModule.class,
                AvsConfigModule.class,
                LiteEnvModule.class,
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
