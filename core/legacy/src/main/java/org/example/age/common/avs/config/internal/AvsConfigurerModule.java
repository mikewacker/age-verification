package org.example.age.common.avs.config.internal;

import dagger.Module;
import dagger.Provides;
import java.security.PrivateKey;
import java.time.Duration;
import javax.inject.Named;
import org.example.age.common.avs.config.AvsConfig;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li><code>@Named("signing") {@link PrivateKey}</code></li>
 *     <li><code>@Named("expiresIn") {@link Duration}</code></li>
 * </ul>
 *
 * <p>Depends on an unbound <code>{@link AvsConfig}</code>.</p>
 */
@Module
public interface AvsConfigurerModule {

    @Provides
    @Named("signing")
    static PrivateKey providePrivateSigningKey(AvsConfig avsConfig) {
        return avsConfig.privateSigningKey();
    }

    @Provides
    @Named("expiresIn")
    static Duration provideExpiresIn(AvsConfig avsConfig) {
        return avsConfig.expiresIn();
    }
}
