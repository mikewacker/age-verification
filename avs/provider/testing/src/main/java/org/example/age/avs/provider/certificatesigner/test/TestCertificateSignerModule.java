package org.example.age.avs.provider.certificatesigner.test;

import dagger.Binds;
import dagger.Module;
import org.example.age.avs.spi.AgeCertificateSigner;

/** Dagger module that binds {@link AgeCertificateSigner}. */
@Module
public abstract class TestCertificateSignerModule {

    @Binds
    abstract AgeCertificateSigner bindAgeCertificateSigner(FakeAgeCertificateSigner impl);

    TestCertificateSignerModule() {}
}
