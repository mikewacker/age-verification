package org.example.age.site.app;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import jakarta.inject.Singleton;
import org.example.age.common.app.env.DropwizardEnvModule;
import org.example.age.common.provider.account.demo.DemoAccountIdModule;
import org.example.age.common.provider.pendingstore.redis.RedisPendingStoreModule;
import org.example.age.site.api.SiteApi;
import org.example.age.site.app.config.SiteAppConfig;
import org.example.age.site.app.config.SiteConfigModule;
import org.example.age.site.client.avs.AvsClientModule;
import org.example.age.site.endpoint.SiteEndpointModule;
import org.example.age.site.provider.accountstore.dynamodb.DynamoDbSiteAccountStoreModule;
import org.example.age.site.provider.certificateverifier.demo.DemoCertificateVerifierModule;
import org.example.age.site.provider.userlocalizer.demo.DemoSiteUserLocalizerModule;

/** Application for a site. */
public final class SiteApp extends Application<SiteAppConfig> {

    /** Runs the application. */
    public static void main(String[] args) throws Exception {
        new SiteApp().run(args);
    }

    @Override
    public String getName() {
        return "site";
    }

    @Override
    public void run(SiteAppConfig appConfig, Environment env) {
        AppComponent component = AppComponent.create(appConfig, env);
        env.jersey().register(component.endpoint());
    }

    /** Dagger component for the application. */
    @Component(
            modules = {
                SiteEndpointModule.class,
                DemoAccountIdModule.class,
                AvsClientModule.class,
                DynamoDbSiteAccountStoreModule.class,
                RedisPendingStoreModule.class,
                DemoCertificateVerifierModule.class,
                DemoSiteUserLocalizerModule.class,
                SiteConfigModule.class,
                DropwizardEnvModule.class,
            })
    @Singleton
    interface AppComponent {

        static AppComponent create(SiteAppConfig appConfig, Environment env) {
            return DaggerSiteApp_AppComponent.factory().create(appConfig, env);
        }

        SiteApi endpoint();

        @Component.Factory
        interface Factory {

            AppComponent create(@BindsInstance SiteAppConfig appConfig, @BindsInstance Environment env);
        }
    }
}
