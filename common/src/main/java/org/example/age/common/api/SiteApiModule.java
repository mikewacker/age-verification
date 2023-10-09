package org.example.age.common.api;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import org.example.age.common.client.RequestDispatcherModule;
import org.example.age.common.config.SiteConfig;
import org.example.age.common.config.SiteConfigurerModule;
import org.example.age.common.verification.AccountIdExtractor;
import org.example.age.common.verification.VerificationManagerModule;
import org.example.age.common.verification.VerificationStore;
import org.example.age.common.verification.auth.AuthManagerModule;
import org.example.age.common.verification.auth.AuthMatchDataExtractor;

/**
 * Dagger module that publishes a binding for <code>@Named("siteApi") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link SiteConfig}</li>
 * </ul>
 */
@Module(
        includes = {
            AuthManagerModule.class,
            VerificationManagerModule.class,
            RequestDispatcherModule.class,
            SiteConfigurerModule.class
        })
public interface SiteApiModule {

    @Binds
    @Named("siteApi")
    HttpHandler bindSiteApiHttpHandler(SiteApiHandler impl);
}
