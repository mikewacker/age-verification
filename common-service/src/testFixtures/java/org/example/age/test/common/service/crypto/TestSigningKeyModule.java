package org.example.age.test.common.service.crypto;

import dagger.Module;
import dagger.Provides;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Dagger module that publishes binding for...
 * <ul>
 *     <li><code>@Named("signing") {@link PrivateKey}</code></li>
 *     <li><code>@Named("signing") {@link PublicKey}</code></li>
 * </ul>
 */
@Module
public interface TestSigningKeyModule {

    @Provides
    @Named("signing")
    @Singleton
    static PrivateKey providePrivateSigningKey() {
        return TestSigningKeyStore.getPrivateKey();
    }

    @Provides
    @Named("signing")
    @Singleton
    static PublicKey providePublicSigningKey() {
        return TestSigningKeyStore.getPublicKey();
    }
}
