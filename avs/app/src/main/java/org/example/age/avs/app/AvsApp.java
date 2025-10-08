package org.example.age.avs.app;

import dagger.BindsInstance;
import dagger.Component;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import jakarta.inject.Singleton;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.app.config.AvsAppConfig;
import org.example.age.avs.app.config.AvsConfigModule;
import org.example.age.avs.client.site.SiteClientsModule;
import org.example.age.avs.endpoint.AvsEndpointModule;
import org.example.age.avs.provider.accountstore.dynamodb.DynamoDbAvsAccountStoreModule;
import org.example.age.avs.provider.certificatesigner.demo.DemoCertificateSignerModule;
import org.example.age.avs.provider.userlocalizer.demo.DemoAvsUserLocalizerModule;
import org.example.age.common.app.env.DropwizardEnvModule;
import org.example.age.common.provider.account.demo.DemoAccountIdModule;
import org.example.age.common.provider.pendingstore.redis.RedisPendingStoreModule;

/** Application for the age verification service. */
public class AvsApp extends Application<AvsAppConfig> {

    /** Runs the application. */
    public static void main(String[] args) throws Exception {
        new AvsApp().run(args);
    }

    @Override
    public String getName() {
        return "avs";
    }

    @Override
    public void run(AvsAppConfig appConfig, Environment env) {
        AppComponent component = AppComponent.create(appConfig, env);
        env.jersey().register(component.endpoint());
    }

    /** Dagger component for the application. */
    @Component(
            modules = {
                AvsEndpointModule.class,
                DemoAccountIdModule.class,
                SiteClientsModule.class,
                DynamoDbAvsAccountStoreModule.class,
                RedisPendingStoreModule.class,
                DemoCertificateSignerModule.class,
                DemoAvsUserLocalizerModule.class,
                AvsConfigModule.class,
                DropwizardEnvModule.class,
            })
    @Singleton
    interface AppComponent {

        static AppComponent create(AvsAppConfig appConfig, Environment env) {
            return DaggerAvsApp_AppComponent.factory().create(appConfig, env);
        }

        AvsApi endpoint();

        @Component.Factory
        interface Factory {

            AppComponent create(@BindsInstance AvsAppConfig appConfig, @BindsInstance Environment env);
        }
    }
}
