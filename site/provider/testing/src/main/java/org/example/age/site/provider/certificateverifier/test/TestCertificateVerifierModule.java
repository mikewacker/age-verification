package org.example.age.site.provider.certificateverifier.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.site.spi.AgeCertificateVerifier;

/** Dagger module that binds {@link AgeCertificateVerifier}. */
@Module
public abstract class TestCertificateVerifierModule {

    @Binds
    abstract AgeCertificateVerifier bindAgeCertificateVerifier(FakeAgeCertificateVerifier impl);

    TestCertificateVerifierModule() {}
}
