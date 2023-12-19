package org.example.age.module.store.avs.test;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.api.def.common.VerificationStatus;
import org.example.age.service.store.common.VerificationStore;
import org.junit.jupiter.api.Test;

public final class TestAvsVerificationStoreTest {

    @Test
    public void getVerifiedPeople() {
        VerificationStore verificationStore = TestComponent.createVerificationStore();
        assertThat(verificationStore.load("John Smith").status()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(verificationStore.load("Billy Smith").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    /** Dagger component that provides a {@link VerificationStore}. */
    @Component(modules = TestAvsVerificationStoreModule.class)
    @Singleton
    interface TestComponent {

        static VerificationStore createVerificationStore() {
            TestComponent component = DaggerTestAvsVerificationStoreTest_TestComponent.create();
            return component.verificationStore();
        }

        VerificationStore verificationStore();
    }
}
