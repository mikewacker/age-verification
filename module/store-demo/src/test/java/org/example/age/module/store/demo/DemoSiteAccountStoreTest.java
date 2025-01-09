package org.example.age.module.store.demo;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.VerifiedUser;
import org.example.age.service.api.store.SiteVerificationStore;
import org.example.age.testing.TestModels;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("unused")
public final class DemoSiteAccountStoreTest {

    private SiteVerificationStore store;

    @BeforeEach
    public void createVerificationStore() {
        TestComponent component = TestComponent.create();
        store = component.verificationStore();
    }

    @Test
    public void saveThenLoad() throws Exception {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId = store.trySave("username", user, expiresIn(5))
                .toCompletableFuture()
                .get();
        assertThat(maybeConflictingAccountId).isEmpty();

        VerificationState state = store.load("username").toCompletableFuture().get();
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.getUser()).isEqualTo(user);
    }

    @Test
    public void loadUnverified() throws Exception {
        VerificationState state = store.load("username").toCompletableFuture().get();
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.UNVERIFIED);
        assertThat(state.getUser()).isNull();
    }

    @Test
    public void loadExpired() throws Exception {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId = store.trySave("username", user, expiresIn(-5))
                .toCompletableFuture()
                .get();
        assertThat(maybeConflictingAccountId).isEmpty();

        VerificationState state = store.load("username").toCompletableFuture().get();
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.EXPIRED);
        assertThat(state.getUser()).isNull();
    }

    @Test
    public void error_DuplicateVerification() throws Exception {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId1 = store.trySave("username1", user, expiresIn(5))
                .toCompletableFuture()
                .get();
        assertThat(maybeConflictingAccountId1).isEmpty();

        Optional<String> maybeConflictingAccountId2 = store.trySave("username2", user, expiresIn(5))
                .toCompletableFuture()
                .get();
        assertThat(maybeConflictingAccountId2).hasValue("username1");

        VerificationState state1 = store.load("username1").toCompletableFuture().get();
        assertThat(state1.getStatus()).isEqualTo(VerificationStatus.VERIFIED);

        VerificationState state2 = store.load("username2").toCompletableFuture().get();
        assertThat(state2.getStatus()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    private static OffsetDateTime expiresIn(int minutes) {
        return OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(minutes));
    }

    /** Dagger component for the store. */
    @Component(modules = DemoSiteAccountStoreModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerDemoSiteAccountStoreTest_TestComponent.create();
        }

        SiteVerificationStore verificationStore();
    }
}
