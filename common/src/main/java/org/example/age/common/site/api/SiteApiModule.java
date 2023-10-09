package org.example.age.common.site.api;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import org.example.age.common.account.AccountIdExtractor;
import org.example.age.common.client.internal.RequestDispatcherModule;
import org.example.age.common.site.auth.AuthMatchDataExtractor;
import org.example.age.common.site.auth.internal.AuthManagerModule;
import org.example.age.common.site.config.SiteConfig;
import org.example.age.common.site.config.internal.SiteConfigurerModule;
import org.example.age.common.site.verification.VerificationStore;
import org.example.age.common.site.verification.internal.VerificationManagerModule;

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
