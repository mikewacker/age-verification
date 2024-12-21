package org.example.age.module.key.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.NoSuchElementException;
import org.example.age.data.crypto.SecureId;
import org.example.age.module.internal.resource.TestSiteResourceModule;
import org.example.age.service.key.RefreshablePseudonymKeyProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class ResourcePseudonymKeyTest {

    private static RefreshablePseudonymKeyProvider keyProvider;

    @BeforeAll
    public static void createRefreshablePseudonymKeyProvider() {
        keyProvider = TestComponent.createRefreshablePseudonymKeyProvider();
    }

    @Test
    public void getPseudonymKey() {
        SecureId pseudonymKey = keyProvider.getPseudonymKey("local");
        assertThat(pseudonymKey).isNotNull();
    }

    @Test
    public void error_KeyNotFound() {
        assertThatThrownBy(() -> keyProvider.getPseudonymKey("dne"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("dne");
    }

    /** Dagger component that provides a {@link RefreshablePseudonymKeyProvider}. */
    @Component(modules = {ResourceSiteKeyModule.class, TestSiteResourceModule.class})
    @Singleton
    interface TestComponent {

        static RefreshablePseudonymKeyProvider createRefreshablePseudonymKeyProvider() {
            TestComponent component = DaggerResourcePseudonymKeyTest_TestComponent.create();
            return component.refreshablePseudonymKeyProvider();
        }

        RefreshablePseudonymKeyProvider refreshablePseudonymKeyProvider();
    }
}
