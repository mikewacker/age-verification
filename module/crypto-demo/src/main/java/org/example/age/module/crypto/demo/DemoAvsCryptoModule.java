package org.example.age.module.crypto.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.service.api.crypto.AgeCertificateSigner;
import org.example.age.service.api.crypto.AvsVerifiedUserLocalizer;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AgeCertificateSigner}
 *     <li>{@link AvsVerifiedUserLocalizer}
 * </ul>
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link AvsKeysConfig}
 *     <li>{@link ObjectMapper}
 * </ul>
 * <p>
 * Loads keys from configuration; it suffices to say that a production application should NOT do this.
 */
@Module
public interface DemoAvsCryptoModule {

    @Binds
    AgeCertificateSigner bindAgeCertificateSigner(DemoAgeCertificateSigner impl);

    @Binds
    AvsVerifiedUserLocalizer bindAvsVerifiedUserLocalizer(DemoAvsVerifiedUserLocalizer impl);
}
