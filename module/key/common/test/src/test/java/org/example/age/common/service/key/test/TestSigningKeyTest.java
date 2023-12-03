package org.example.age.common.service.key.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.data.crypto.DigitalSignature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class TestSigningKeyTest {

    private static Provider<PrivateKey> privateSigningKeyProvider;
    private static Provider<PublicKey> publicSigningKeyProvider;

    @BeforeAll
    public static void createSigningKeyProviders() {
        privateSigningKeyProvider = TestAvsComponent.createPrivateSigningKeyProvider();
        publicSigningKeyProvider = TestSiteComponent.createPublicSigningKeyProvider();
    }

    @Test
    public void signThenVerify() {
        byte[] message = "message".getBytes(StandardCharsets.UTF_8);
        DigitalSignature signature = DigitalSignature.sign(message, privateSigningKeyProvider.get());
        boolean wasVerified = signature.verify(message, publicSigningKeyProvider.get());
        assertThat(wasVerified).isTrue();
    }

    @Component(modules = TestKeyModule.class)
    @Singleton
    interface TestAvsComponent {

        static Provider<PrivateKey> createPrivateSigningKeyProvider() {
            TestAvsComponent component = DaggerTestSigningKeyTest_TestAvsComponent.create();
            return component.privateSigningKeyProvider();
        }

        @Named("signing")
        Provider<PrivateKey> privateSigningKeyProvider();
    }

    @Component(modules = TestKeyModule.class)
    @Singleton
    interface TestSiteComponent {

        static Provider<PublicKey> createPublicSigningKeyProvider() {
            TestSiteComponent component = DaggerTestSigningKeyTest_TestSiteComponent.create();
            return component.publicSigningKeyProvider();
        }

        @Named("signing")
        Provider<PublicKey> publicSigningKeyProvider();
    }
}
