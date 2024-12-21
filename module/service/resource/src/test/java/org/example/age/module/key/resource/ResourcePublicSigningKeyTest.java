package org.example.age.module.key.resource;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.security.PublicKey;
import org.example.age.module.internal.resource.TestSiteResourceModule;
import org.example.age.service.key.RefreshablePublicSigningKeyProvider;
import org.junit.jupiter.api.Test;

public final class ResourcePublicSigningKeyTest {

    @Test
    public void getPublicSigningKey() {
        RefreshablePublicSigningKeyProvider keyProvider = TestComponent.createRefreshablePublicSigningKeyProvider();
        PublicKey publicKey = keyProvider.getPublicSigningKey();
        assertThat(publicKey).isNotNull();
    }

    /** Dagger component that provides a {@link RefreshablePublicSigningKeyProvider}. */
    @Component(modules = {ResourceSiteKeyModule.class, TestSiteResourceModule.class})
    @Singleton
    interface TestComponent {

        static RefreshablePublicSigningKeyProvider createRefreshablePublicSigningKeyProvider() {
            TestComponent component = DaggerResourcePublicSigningKeyTest_TestComponent.create();
            return component.refreshablePublicSigningKeyProvider();
        }

        RefreshablePublicSigningKeyProvider refreshablePublicSigningKeyProvider();
    }
}
