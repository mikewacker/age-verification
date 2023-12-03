package org.example.age.common.service.key.test;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.common.service.key.PseudonymKeyProvider;

/**
 * Dagger module that publishes bindings for...
 * <ul>
 *     <li><code>@Named("signing") {@link PrivateKey}</code></li>
 *     <li><code>@Named("signing") {@link PublicKey}</code></li>
 *     <li>{@link PseudonymKeyProvider}</li>
 * </ul>
 */
@Module
public interface TestKeyModule {

    @Provides
    @Named("signing")
    @Singleton
    static PrivateKey providePrivateSigningKey() {
        return TestSigningKeys.privateKey();
    }

    @Provides
    @Named("signing")
    @Singleton
    static PublicKey providePublicPseudonymKey() {
        return TestSigningKeys.publicKey();
    }

    @Binds
    PseudonymKeyProvider bindPseudonymKeyProvider(TestPseudonymKeys impl);
}
