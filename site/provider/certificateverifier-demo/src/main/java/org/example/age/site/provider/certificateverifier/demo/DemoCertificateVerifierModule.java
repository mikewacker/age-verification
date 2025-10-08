package org.example.age.site.provider.certificateverifier.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.common.env.BaseEnvModule;
import org.example.age.common.env.LiteEnv;
import org.example.age.common.provider.signingkey.demo.EcPublicKeyConfig;
import org.example.age.site.spi.AgeCertificateVerifier;

/**
 * Dagger module that binds {@link AgeCertificateVerifier}.
 * <p>
 * Depends on an unbound...
 * <ul>
 *     <li>{@link EcPublicKeyConfig}
 *     <li>{@link LiteEnv}
 * </ul>
 * <p>
 * Uses a NIST P-256 key.
 */
@Module(includes = BaseEnvModule.class)
public abstract class DemoCertificateVerifierModule {

    @Binds
    abstract AgeCertificateVerifier bindAgeCertificateVerifier(DemoAgeCertificateVerifier impl);

    DemoCertificateVerifierModule() {}
}
