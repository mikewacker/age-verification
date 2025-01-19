package org.example.age.module.store.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.CompletionStageTesting.getCompleted;

import dagger.Component;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.VerifiedUser;
import org.example.age.service.module.store.SiteVerificationStore;
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
    public void saveThenLoad() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId = getCompleted(store.trySave("username", user, expiresIn(5)));
        assertThat(maybeConflictingAccountId).isEmpty();

        VerificationState state = getCompleted(store.load("username"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.getUser()).isEqualTo(user);
    }

    @Test
    public void saveTwiceForSameAccount() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId1 = getCompleted(store.trySave("username", user, expiresIn(5)));
        assertThat(maybeConflictingAccountId1).isEmpty();

        Optional<String> maybeConflictingAccountId2 = getCompleted(store.trySave("username", user, expiresIn(10)));
        assertThat(maybeConflictingAccountId2).isEmpty();
    }

    @Test
    public void loadUnverified() {
        VerificationState state = getCompleted(store.load("username"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.UNVERIFIED);
        assertThat(state.getUser()).isNull();
    }

    @Test
    public void loadExpired() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId = getCompleted(store.trySave("username", user, expiresIn(-5)));
        assertThat(maybeConflictingAccountId).isEmpty();

        VerificationState state = getCompleted(store.load("username"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.EXPIRED);
        assertThat(state.getUser()).isNull();
    }

    @Test
    public void error_DuplicateVerification() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId1 = getCompleted(store.trySave("username1", user, expiresIn(5)));
        assertThat(maybeConflictingAccountId1).isEmpty();

        Optional<String> maybeConflictingAccountId2 = getCompleted(store.trySave("username2", user, expiresIn(5)));
        assertThat(maybeConflictingAccountId2).hasValue("username1");

        VerificationState state1 = getCompleted(store.load("username1"));
        assertThat(state1.getStatus()).isEqualTo(VerificationStatus.VERIFIED);

        VerificationState state2 = getCompleted(store.load("username2"));
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
