package org.example.age.module.store.resource;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.api.def.VerificationStatus;
import org.example.age.module.internal.resource.TestAvsResourceModule;
import org.example.age.service.store.VerificationStore;
import org.junit.jupiter.api.Test;

public final class ResourceAvsVerificationStoreTest {

    @Test
    public void getVerifiedPeople() {
        VerificationStore verificationStore = TestComponent.createVerificationStore();
        assertThat(verificationStore.load("John Smith").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    /** Dagger component that provides a {@link VerificationStore}. */
    @Component(modules = {ResourceAvsVerificationStoreModule.class, TestAvsResourceModule.class})
    @Singleton
    interface TestComponent {

        static VerificationStore createVerificationStore() {
            TestComponent component = DaggerResourceAvsVerificationStoreTest_TestComponent.create();
            return component.verificationStore();
        }

        VerificationStore verificationStore();
    }
}
