package org.example.age.module.store.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.api.def.VerificationState;
import org.example.age.api.def.VerificationStatus;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.service.store.VerificationStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class InMemoryVerificationStoreTest {

    private VerificationStore verificationStore;

    @BeforeEach
    public void createVerificationStore() {
        verificationStore = TestComponent.createVerificationStore();
    }

    @Test
    public void load_Unverified() {
        VerificationState state = verificationStore.load("username");
        assertThat(state.status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void verify() {
        VerificationState state = VerificationState.verified(createVerifiedUser(), createExpiration());
        Optional<String> maybeConflictingAccountId = verificationStore.trySave("username", state);
        assertThat(maybeConflictingAccountId).isEmpty();
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    public void reverify() {
        VerificationState state = VerificationState.verified(createVerifiedUser(), createExpiration());
        verificationStore.trySave("username", state);
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);

        Optional<String> maybeConflictingAccountId = verificationStore.trySave("username", state);
        assertThat(maybeConflictingAccountId).isEmpty();
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    public void verifyFails_DuplicationVerification() {
        VerificationState state = VerificationState.verified(createVerifiedUser(), createExpiration());
        verificationStore.trySave("username1", state);
        assertThat(verificationStore.load("username1").status()).isEqualTo(VerificationStatus.VERIFIED);

        Optional<String> maybeConflictingAccountId = verificationStore.trySave("username2", state);
        assertThat(maybeConflictingAccountId).hasValue("username1");
        assertThat(verificationStore.load("username2").status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void delete() {
        VerificationState state = VerificationState.verified(createVerifiedUser(), createExpiration());
        verificationStore.trySave("username", state);
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);

        verificationStore.delete("username");
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void load_UpdateState() {
        VerificationState state = VerificationState.verified(createVerifiedUser(), createExpiration(-10));
        verificationStore.trySave("username", state);
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.EXPIRED);
    }

    @Test
    public void verify_DuplicateVerificationExpired() {
        VerifiedUser user = createVerifiedUser();
        VerificationState state1 = VerificationState.verified(user, createExpiration(-10));
        verificationStore.trySave("username1", state1);

        VerificationState state2 = VerificationState.verified(user, createExpiration());
        Optional<String> maybeConflictingAccountId = verificationStore.trySave("username2", state2);
        assertThat(maybeConflictingAccountId).isEmpty();
        assertThat(verificationStore.load("username2").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    private static VerifiedUser createVerifiedUser() {
        return VerifiedUser.of(SecureId.generate(), 18);
    }

    private static long createExpiration() {
        return createExpiration(10);
    }

    private static long createExpiration(long expiresIn) {
        long now = System.currentTimeMillis() / 1000;
        return now + expiresIn;
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
