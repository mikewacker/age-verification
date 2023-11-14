package org.example.age.common.site.store;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.data.VerifiedUser;
import org.example.age.data.crypto.SecureId;
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
    public void verify() {
        VerificationState state = createVerifiedState();
        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username", state);
        assertThat(maybeDuplicateAccountId).isEmpty();
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    public void verifySameAccountTwice() {
        VerificationState state = createVerifiedState();
        verificationStore.trySave("username", state);
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);

        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username", state);
        assertThat(maybeDuplicateAccountId).isEmpty();
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    public void updateState() {
        long past = (System.currentTimeMillis() / 1000) - 10;
        VerificationState state = createVerifiedState(past);
        verificationStore.trySave("username", state);
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.EXPIRED);
    }

    @Test
    public void switchVerifiedAccount() {
        VerificationState state = createVerifiedState();
        verificationStore.trySave("username1", state);
        assertThat(verificationStore.load("username1").status()).isEqualTo(VerificationStatus.VERIFIED);

        verificationStore.trySave("username1", VerificationState.invalidated());
        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username2", state);
        assertThat(maybeDuplicateAccountId).isEmpty();
        assertThat(verificationStore.load("username1").status()).isEqualTo(VerificationStatus.INVALIDATED);
        assertThat(verificationStore.load("username2").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    public void delete() {
        VerificationState state = createVerifiedState();
        verificationStore.trySave("username", state);
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);

        verificationStore.delete("username");
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void error_DuplicateVerification() {
        VerificationState state = createVerifiedState();
        verificationStore.trySave("username1", state);
        assertThat(verificationStore.load("username1").status()).isEqualTo(VerificationStatus.VERIFIED);

        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username2", state);
        assertThat(maybeDuplicateAccountId).hasValue("username1");
        assertThat(verificationStore.load("username2").status()).isEqualTo(VerificationStatus.UNVERIFIED);
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
