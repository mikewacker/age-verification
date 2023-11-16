package org.example.age.common.site.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.common.api.data.AccountIdExtractor;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.common.base.client.internal.RequestDispatcherModule;
import org.example.age.common.base.store.PendingStoreFactory;
import org.example.age.common.site.auth.internal.AuthManagerModule;
import org.example.age.common.site.config.SiteConfig;
import org.example.age.common.site.config.internal.SiteConfigurerModule;
import org.example.age.common.site.store.VerificationStore;
import org.example.age.common.site.verification.internal.VerificationManagerModule;
import org.example.age.data.utils.DataMapper;

/**
 * Dagger module that publishes a binding for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link VerificationStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li><code>Provider&lt;{@link SiteConfig}&gt;</code></li>
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
    @Named("api")
    HttpHandler bindApiHttpHandler(SiteApiHandler impl);

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return DataMapper.get();
    }
}
