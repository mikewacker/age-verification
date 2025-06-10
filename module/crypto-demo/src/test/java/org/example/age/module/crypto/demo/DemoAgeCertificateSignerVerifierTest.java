package org.example.age.module.crypto.demo;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.module.crypto.AgeCertificateSigner;
import org.example.age.service.module.crypto.AgeCertificateVerifier;
import org.example.age.service.module.crypto.testing.AgeCertificateSignerVerifierTestTemplate;

public final class DemoAgeCertificateSignerVerifierTest extends AgeCertificateSignerVerifierTestTemplate {

    private static final AgeCertificateSigner signer = TestAvsComponent.create();
    private static final AgeCertificateVerifier verifier = TestSiteComponent.create();

    @Override
    protected AgeCertificateSigner signer() {
        return signer;
    }

    @Override
    protected AgeCertificateVerifier verifier() {
        return verifier;
    }

    /** Dagger component for {@link AgeCertificateSigner}. */
    @Component(modules = {DemoAvsCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestAvsComponent extends Supplier<AgeCertificateSigner> {

        static AgeCertificateSigner create() {
            return DaggerDemoAgeCertificateSignerVerifierTest_TestAvsComponent.create()
                    .get();
        }
    }

    /** Dagger component for {@link AgeCertificateVerifier}. */
    @Component(modules = {DemoSiteCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestSiteComponent extends Supplier<AgeCertificateVerifier> {

        static AgeCertificateVerifier create() {
            return DaggerDemoAgeCertificateSignerVerifierTest_TestSiteComponent.create()
                    .get();
        }
    }
}
