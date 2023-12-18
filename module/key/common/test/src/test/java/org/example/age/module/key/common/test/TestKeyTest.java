package org.example.age.module.key.common.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.nio.charset.StandardCharsets;
import javax.inject.Singleton;
import org.example.age.data.crypto.DigitalSignature;
import org.example.age.data.crypto.SecureId;
import org.example.age.service.key.common.RefreshableKeyProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class TestKeyTest {

    private static RefreshableKeyProvider siteKeyProvider;
    private static RefreshableKeyProvider avsKeyProvider;

    @BeforeAll
    public static void createRefreshableKeyProviders() {
        siteKeyProvider = TestComponent.createRefreshableKeyProvider();
        avsKeyProvider = TestComponent.createRefreshableKeyProvider();
    }

    @Test
    public void getSigningKeys() {
        byte[] message = "Hello, world!".getBytes(StandardCharsets.UTF_8);
        DigitalSignature signature = DigitalSignature.sign(message, avsKeyProvider.getPrivateSigningKey());
        boolean wasVerified = signature.verify(message, siteKeyProvider.getPublicSigningKey());
        assertThat(wasVerified).isTrue();
    }

    @Test
    public void getPseudonymKeys() {
        SecureId pseudonymKey1 = avsKeyProvider.getPseudonymKey("name1");
        SecureId pseudonymKey2 = avsKeyProvider.getPseudonymKey("name2");
        assertThat(pseudonymKey1).isNotEqualTo(pseudonymKey2);

        SecureId pseudonymKey3 = avsKeyProvider.getPseudonymKey("name1");
        assertThat(pseudonymKey1).isEqualTo(pseudonymKey3);
    }

    /** Dagger component that provides a {@link RefreshableKeyProvider}. */
    @Component(modules = TestKeyModule.class)
    @Singleton
    interface TestComponent {

        static RefreshableKeyProvider createRefreshableKeyProvider() {
            TestComponent component = DaggerTestKeyTest_TestComponent.create();
            return component.refreshableKeyProvider();
        }

        RefreshableKeyProvider refreshableKeyProvider();
    }
}
