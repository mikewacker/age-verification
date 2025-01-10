package org.example.age.app;

import dagger.BindsInstance;
import dagger.Component;
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
import org.example.age.module.store.demo.DemoSiteAccountStoreModule;
import org.example.age.module.store.inmemory.InMemoryPendingStoreModule;
import org.example.age.service.SiteServiceModule;
import org.example.age.service.module.request.RequestContextProvider;

/** Application for a site. */
public final class SiteApp extends NamedApp<SiteAppConfig> {

    /** Creates ands runs an application. */
    public static void run(String name, String appConfigPath) throws Exception {
        new SiteApp(name).runServer(appConfigPath);
    }

    /** Creates a named application. */
    public SiteApp(String name) {
        super(name);
    }

    /** Creates an application with the default name. Provided for the purpose of testing. */
    public SiteApp() {
        this("site");
    }

    @Override
    public void run(SiteAppConfig appConfig, Environment env) {
        AppComponent component = AppComponent.create(appConfig, env);
        env.jersey().register(component.service());
        env.jersey().register(component.requestContextProvider());
    }

    /** Dagger component for the application. */
    @Component(
            modules = {
                SiteServiceModule.class,
                DemoAccountIdModule.class,
                SiteClientModule.class,
                DemoSiteAccountStoreModule.class,
                InMemoryPendingStoreModule.class,
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

        RequestContextProvider requestContextProvider();

        @Component.Factory
        interface Factory {

            AppComponent create(@BindsInstance SiteAppConfig appConfig, @BindsInstance Environment env);
        }
    }
}
