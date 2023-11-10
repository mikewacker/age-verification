package org.example.age.common.avs.api;

import dagger.Binds;
import dagger.Module;
import io.undertow.server.HttpHandler;
import javax.inject.Named;
import org.example.age.common.api.data.AccountIdExtractor;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.common.avs.config.AvsConfig;
import org.example.age.common.avs.config.internal.AvsConfigurerModule;
import org.example.age.common.avs.store.RegisteredSiteConfigStore;
import org.example.age.common.avs.store.VerifiedUserStore;
import org.example.age.common.avs.verification.internal.VerificationManagerModule;
import org.example.age.common.base.client.internal.RequestDispatcherModule;
import org.example.age.common.base.store.PendingStoreFactory;

/**
 * Dagger module that publishes a binding for <code>@Named("api") {@link HttpHandler}</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link AccountIdExtractor}</li>
 *     <li>{@link AuthMatchDataExtractor}</li>
 *     <li>{@link RegisteredSiteConfigStore}</li>
 *     <li>{@link VerifiedUserStore}</li>
 *     <li>{@link PendingStoreFactory}</li>
 *     <li><code>Provider&lt;{@link AvsConfig}&gt;</code></li>
 * </ul>
 */
@Module(includes = {VerificationManagerModule.class, RequestDispatcherModule.class, AvsConfigurerModule.class})
public interface AvsApiModule {

    @Binds
    @Named("api")
    HttpHandler bindApiHttpHandler(AvsApiHandler impl);
}
