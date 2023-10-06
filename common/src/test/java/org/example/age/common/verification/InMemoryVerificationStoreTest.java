package org.example.age.common.verification;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class InMemoryVerificationStoreTest {

    private VerificationStore verificationStore;

    @BeforeEach
    public void createVerificationStore() {
        verificationStore = TestComponent.createVerificationStore();
    }

    @Test
    public void loadUnverified() {
        VerificationState state = verificationStore.load("username");
        assertThat(state.status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void saveAndLoad() {
        VerificationState state = createVerifiedState();
        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username", state);
        assertThat(maybeDuplicateAccountId).isEmpty();
        VerificationState loadedState = verificationStore.load("username");
        assertThat(loadedState.status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    public void saveTwiceForSameAccount() {
        VerificationState state = createVerifiedState();
        verificationStore.trySave("username", state);

        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username", state);
        assertThat(maybeDuplicateAccountId).isEmpty();
    }

    @Test
    public void saveUpdateAndLoad() {
        long past = (System.currentTimeMillis() / 1000) - 10;
        VerificationState state = createVerifiedState(past);
        verificationStore.trySave("username", state);
        VerificationState loadedState = verificationStore.load("username");
        assertThat(loadedState.status()).isEqualTo(VerificationStatus.EXPIRED);
    }

    @Test
    public void switchVerifiedAccount() {
        VerificationState state = createVerifiedState();
        verificationStore.trySave("username1", state);

        verificationStore.trySave("username1", VerificationState.invalidated());
        VerificationState loadedState1 = verificationStore.load("username1");
        assertThat(loadedState1.status()).isEqualTo(VerificationStatus.INVALIDATED);

        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username2", state);
        assertThat(maybeDuplicateAccountId).isEmpty();
        VerificationState loadedState2 = verificationStore.load("username2");
        assertThat(loadedState2.status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    public void failToSaveDuplicateVerification() {
        VerificationState state = createVerifiedState();
        verificationStore.trySave("username1", state);

        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username2", state);
        assertThat(maybeDuplicateAccountId).hasValue("username1");
        VerificationState loadedState = verificationStore.load("username2");
        assertThat(loadedState.status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    private static VerificationState createVerifiedState() {
        long future = (System.currentTimeMillis() / 1000) + 10;
        return createVerifiedState(future);
    }

    private static VerificationState createVerifiedState(long expiration) {
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        return VerificationState.verified(user, expiration);
    }

    /** Dagger component that provides a {@link VerificationStore}. */
    @Component(modules = InMemoryVerificationStoreModule.class)
    @Singleton
    interface TestComponent {

        static VerificationStore createVerificationStore() {
            TestComponent component = DaggerInMemoryVerificationStoreTest_TestComponent.create();
            return component.verificationStore();
        }

        VerificationStore verificationStore();
    }
}
