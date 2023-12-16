package org.example.age.module.store.avs.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.api.def.common.VerificationStatus;
import org.example.age.service.store.common.VerificationStore;
import org.junit.jupiter.api.Test;

public final class TestVerificationStoreTest {

    @Test
    public void getVerifiedPeople() {
        VerificationStore verificationStore = TestComponent.createVerificationStore();
        assertThat(verificationStore.load("John Smith").status()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(verificationStore.load("Billy Smith").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    /** Dagger component that provides a {@link VerificationStore}. */
    @Component(modules = TestVerificationStoreModule.class)
    @Singleton
    interface TestComponent {

        static VerificationStore createVerificationStore() {
            TestComponent component = DaggerTestVerificationStoreTest_TestComponent.create();
            return component.verificationStore();
        }

        VerificationStore verificationStore();
    }
}
