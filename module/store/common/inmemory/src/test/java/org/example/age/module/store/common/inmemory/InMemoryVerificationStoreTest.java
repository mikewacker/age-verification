package org.example.age.module.store.common.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.api.common.VerificationState;
import org.example.age.api.common.VerificationStatus;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.module.store.common.VerificationStore;
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
        VerificationState state = createAndVerifyUser();
        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username", state);
        assertThat(maybeDuplicateAccountId).isEmpty();
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    public void verifySameAccountTwice() {
        VerificationState state = createAndVerifyUser();
        verificationStore.trySave("username", state);
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);

        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username", state);
        assertThat(maybeDuplicateAccountId).isEmpty();
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    public void updateState() {
        VerificationState state = createAndVerifyUser(-10);
        verificationStore.trySave("username", state);
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.EXPIRED);
    }

    @Test
    public void switchVerifiedAccount() {
        VerificationState state = createAndVerifyUser();
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
        VerificationState state = createAndVerifyUser();
        verificationStore.trySave("username", state);
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.VERIFIED);

        verificationStore.delete("username");
        assertThat(verificationStore.load("username").status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void error_DuplicateVerification() {
        VerificationState state = createAndVerifyUser();
        verificationStore.trySave("username1", state);
        assertThat(verificationStore.load("username1").status()).isEqualTo(VerificationStatus.VERIFIED);

        Optional<String> maybeDuplicateAccountId = verificationStore.trySave("username2", state);
        assertThat(maybeDuplicateAccountId).hasValue("username1");
        assertThat(verificationStore.load("username2").status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    private static VerificationState createAndVerifyUser() {
        return createAndVerifyUser(10);
    }

    private static VerificationState createAndVerifyUser(long expiresIn) {
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        long now = System.currentTimeMillis() / 1000;
        long expiration = now + expiresIn;
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
