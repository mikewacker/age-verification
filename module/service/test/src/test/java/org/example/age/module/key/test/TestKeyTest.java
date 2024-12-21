package org.example.age.module.key.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.nio.charset.StandardCharsets;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.key.RefreshablePrivateSigningKeyProvider;
import org.example.age.service.key.RefreshablePseudonymKeyProvider;
import org.example.age.service.key.RefreshablePublicSigningKeyProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class TestKeyTest {

    private static RefreshablePrivateSigningKeyProvider privateSigningKeyProvider;
    private static RefreshablePublicSigningKeyProvider publicSigningKeyProvider;
    private static RefreshablePseudonymKeyProvider pseudonymKeyProvider;

    @BeforeAll
    public static void createRefreshableKeyProviders() {
        TestComponent siteComponent = TestComponent.create();
        TestComponent avsComponent = TestComponent.create();
        privateSigningKeyProvider = avsComponent.refreshablePrivateSigningKeyProvider();
        publicSigningKeyProvider = siteComponent.refreshablePublicSigningKeyProvider();
        pseudonymKeyProvider = avsComponent.refreshablePseudonymKeyProvider();
    }

    @Test
    public void getSigningKeys() {
        byte[] message = "Hello, world!".getBytes(StandardCharsets.UTF_8);
        DigitalSignature signature = DigitalSignature.sign(message, privateSigningKeyProvider.getPrivateSigningKey());
        boolean wasVerified = signature.verify(message, publicSigningKeyProvider.getPublicSigningKey());
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void getPseudonymKeys() {
        SecureId pseudonymKey1 = pseudonymKeyProvider.getPseudonymKey("name1");
        SecureId pseudonymKey2 = pseudonymKeyProvider.getPseudonymKey("name2");
        assertThat(pseudonymKey1).isNotEqualTo(pseudonymKey2);

        SecureId pseudonymKey3 = pseudonymKeyProvider.getPseudonymKey("name1");
        assertThat(pseudonymKey1).isEqualTo(pseudonymKey3);
    }

    /**
     * Dagger component that provides a...
     * <ul>
     *     <li>{@link RefreshablePrivateSigningKeyProvider}</li>
     *     <li>{@link RefreshablePublicSigningKeyProvider}</li>
     *     <li>{@link RefreshablePseudonymKeyProvider}</li>
     * </ul>
     */
    @Component(modules = TestKeyModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerTestKeyTest_TestComponent.create();
        }

        RefreshablePrivateSigningKeyProvider refreshablePrivateSigningKeyProvider();

        RefreshablePublicSigningKeyProvider refreshablePublicSigningKeyProvider();

        RefreshablePseudonymKeyProvider refreshablePseudonymKeyProvider();
    }
}
