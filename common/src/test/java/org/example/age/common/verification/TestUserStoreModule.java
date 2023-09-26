package org.example.age.common.verification;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import java.time.Duration;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Publishes a test binding for {@link VerifiedUserStore}
 * (and <code>@Named("expiresIn") {@link Supplier}&lt;{@link Duration}&gt;</code>). */
@Module
public interface TestUserStoreModule {

    @Binds
    VerifiedUserStore bindVerifiedUserStore(TestVerifiedUserStore impl);

    @Provides
    @Singleton
    @Named("expiresIn")
    static Supplier<Duration> provideExpiresInDurationSupplier() {
        return () -> Duration.ofHours(1);
    }
}
