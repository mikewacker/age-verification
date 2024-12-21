package org.example.age.module.key.resource;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.security.PrivateKey;
import org.example.age.module.internal.resource.TestAvsResourceModule;
import org.example.age.service.key.RefreshablePrivateSigningKeyProvider;
import org.junit.jupiter.api.Test;

public final class ResourcePrivateSigningKeyTest {

    @Test
    public void getPrivateSigningKey() {
        RefreshablePrivateSigningKeyProvider keyProvider = TestComponent.createRefreshablePrivateSigningKeyProvider();
        PrivateKey privateKey = keyProvider.getPrivateSigningKey();
        assertThat(privateKey).isNotNull();
    }

    /** Dagger component that provides a {@link RefreshablePrivateSigningKeyProvider}. */
    @Component(modules = {ResourceAvsKeyModule.class, TestAvsResourceModule.class})
    @Singleton
    interface TestComponent {

        static RefreshablePrivateSigningKeyProvider createRefreshablePrivateSigningKeyProvider() {
            TestComponent component = DaggerResourcePrivateSigningKeyTest_TestComponent.create();
            return component.refreshablePrivateSigningKeyProvider();
        }

        RefreshablePrivateSigningKeyProvider refreshablePrivateSigningKeyProvider();
    }
}
