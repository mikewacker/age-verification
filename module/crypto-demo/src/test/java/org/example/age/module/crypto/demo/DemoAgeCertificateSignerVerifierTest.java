package org.example.age.module.crypto.demo;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.module.crypto.AgeCertificateSigner;
import org.example.age.service.module.crypto.AgeCertificateVerifier;
import org.example.age.service.module.crypto.testing.AgeCertificateSignerVerifierTestTemplate;
import org.junit.jupiter.api.BeforeAll;

public final class DemoAgeCertificateSignerVerifierTest extends AgeCertificateSignerVerifierTestTemplate {

    private static AgeCertificateSigner signer;
    private static AgeCertificateVerifier verifier;

    @BeforeAll
    public static void createAgeCertificateSignerAndVerifier() {
        TestAvsComponent avsComponent = TestAvsComponent.create();
        signer = avsComponent.ageCertificateSigner();
        TestSiteComponent siteComponent = TestSiteComponent.create();
        verifier = siteComponent.ageCertificateVerifier();
    }

    @Override
    protected AgeCertificateSigner signer() {
        return signer;
    }

    @Override
    protected AgeCertificateVerifier verifier() {
        return verifier;
    }

    /** Dagger component for crypto. */
    @Component(modules = {DemoAvsCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestAvsComponent {

        static TestAvsComponent create() {
            return DaggerDemoAgeCertificateSignerVerifierTest_TestAvsComponent.create();
        }

        AgeCertificateSigner ageCertificateSigner();
    }

    /** Dagger component for crypto. */
    @Component(modules = {DemoSiteCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestSiteComponent {

        static TestSiteComponent create() {
            return DaggerDemoAgeCertificateSignerVerifierTest_TestSiteComponent.create();
        }

        AgeCertificateVerifier ageCertificateVerifier();
    }
}
