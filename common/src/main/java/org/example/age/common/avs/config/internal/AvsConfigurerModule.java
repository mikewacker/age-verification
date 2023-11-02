package org.example.age.common.avs.config.internal;

import dagger.Module;
import dagger.Provides;
import java.security.PrivateKey;
import java.time.Duration;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.common.avs.config.AvsConfig;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li><code>@Named("signing") Supplier&lt;{@link PrivateKey}&gt;</code></li>
 *     <li><code>@Named("expiresIn") Supplier&lt;{@link Duration}&gt;</code></li>
 * </ul>
 *
 * <p>Depends on an unbound <code>Supplier&lt;{@link AvsConfig}&gt;</code>.</p>
 */
@Module
public interface AvsConfigurerModule {

    @Provides
    @Named("signing")
    @Singleton
    static Supplier<PrivateKey> providePrivateSigningKey(Supplier<AvsConfig> avsConfigSupplier) {
        return () -> avsConfigSupplier.get().privateSigningKey();
    }

    @Provides
    @Named("expiresIn")
    @Singleton
    static Supplier<Duration> provideExpiresIn(Supplier<AvsConfig> avsConfigSupplier) {
        return () -> avsConfigSupplier.get().expiresIn();
    }
}
