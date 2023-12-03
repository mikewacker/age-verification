package org.example.age.common.service.key.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.common.service.key.PseudonymKeyProvider;
import org.example.age.data.crypto.SecureId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class TestPseudonymKeyTest {

    private static PseudonymKeyProvider pseudonymKeyProvider;

    @BeforeAll
    public static void createPseudonymKeyProvider() {
        pseudonymKeyProvider = TestComponent.createPseudonymKeyProvider();
    }

    @Test
    public void get() {
        SecureId pseudonymKey1 = pseudonymKeyProvider.get("name1");
        SecureId pseudonymKey2 = pseudonymKeyProvider.get("name2");
        assertThat(pseudonymKey1).isNotEqualTo(pseudonymKey2);

        SecureId pseudonymKey3 = pseudonymKeyProvider.get("name1");
        assertThat(pseudonymKey1).isEqualTo(pseudonymKey3);
    }

    @Component(modules = TestKeyModule.class)
    @Singleton
    interface TestComponent {

        static PseudonymKeyProvider createPseudonymKeyProvider() {
            TestComponent component = DaggerTestPseudonymKeyTest_TestComponent.create();
            return component.pseudonymKeyProvider();
        }

        PseudonymKeyProvider pseudonymKeyProvider();
    }
}
