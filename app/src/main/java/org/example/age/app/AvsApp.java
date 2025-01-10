package org.example.age.app;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.setup.Environment;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.example.age.api.AvsApi;
import org.example.age.app.config.AvsAppConfig;
import org.example.age.app.config.AvsConfigModule;
import org.example.age.app.env.EnvModule;
import org.example.age.module.client.AvsClientModule;
import org.example.age.module.crypto.demo.DemoAvsCryptoModule;
import org.example.age.module.request.demo.DemoAccountIdModule;
import org.example.age.module.store.demo.DemoAvsAccountStoreModule;
import org.example.age.module.store.inmemory.InMemoryPendingStoreModule;
import org.example.age.service.AvsServiceModule;
import org.example.age.service.api.request.RequestContextProvider;

/** Application for the age verification service. */
public class AvsApp extends NamedApp<AvsAppConfig> {

    /** Creates and runs an application. */
    public static void run(String name, String appConfigPath) throws Exception {
        new AvsApp(name).runServer(appConfigPath);
    }

    /** Creates a named application. */
    public AvsApp(String name) {
        super(name);
    }

    /** Creates an application with the default name. Provided for the purpose of testing. */
    public AvsApp() {
        this("avs");
    }

    @Override
    public void run(AvsAppConfig appConfig, Environment env) {
        AppComponent component = AppComponent.create(appConfig, env);
        env.jersey().register(component.service());
        env.jersey().register(component.requestContextProvider());
    }

    /** Dagger component for the application. */
    @Component(
            modules = {
                AvsServiceModule.class,
                DemoAccountIdModule.class,
                AvsClientModule.class,
                DemoAvsAccountStoreModule.class,
                InMemoryPendingStoreModule.class,
                DemoAvsCryptoModule.class,
                AvsConfigModule.class,
                EnvModule.class,
            })
    @Singleton
    interface AppComponent {

        static AppComponent create(AvsAppConfig appConfig, Environment env) {
            return DaggerAvsApp_AppComponent.factory().create(appConfig, env);
        }

        @Named("service")
        AvsApi service();

        RequestContextProvider requestContextProvider();

        @Component.Factory
        interface Factory {

            AppComponent create(@BindsInstance AvsAppConfig appConfig, @BindsInstance Environment env);
        }
    }
}
