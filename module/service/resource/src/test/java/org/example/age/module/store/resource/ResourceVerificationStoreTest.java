package org.example.age.module.store.resource;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.api.def.VerificationStatus;
import org.example.age.module.internal.resource.TestAvsResourceModule;
import org.example.age.service.store.VerificationStore;
import org.junit.jupiter.api.Test;

public final class ResourceVerificationStoreTest {

    @Test
    public void getVerifiedPeople() {
        VerificationStore verificationStore = TestComponent.createVerificationStore();
        assertThat(verificationStore.load("John Smith").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    /** Dagger component that provides a {@link VerificationStore}. */
    @Component(modules = {ResourceVerificationStoreModule.class, TestAvsResourceModule.class}) // Site also works.
    @Singleton
    interface TestComponent {

        static VerificationStore createVerificationStore() {
            TestComponent component = DaggerResourceVerificationStoreTest_TestComponent.create();
            return component.verificationStore();
        }

        VerificationStore verificationStore();
    }
}
