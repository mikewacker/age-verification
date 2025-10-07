package org.example.age.avs.provider.certificatesigner.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.common.provider.signingkey.demo.EcPrivateKeyConfig;

/**
 * Dagger module that binds {@link AgeCertificateSigner}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link EcPrivateKeyConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 * <p>
 * Uses a NIST P-256 key.
 */
@Module(includes = BaseEnvModule.class)
public abstract class DemoCertificateSignerModule {

    @Binds
    abstract AgeCertificateSigner bindAgeCertificateSigner(DemoAgeCertificateSigner impl);

    DemoCertificateSignerModule() {}
}
