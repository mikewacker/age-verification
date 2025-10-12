package org.example.age.testing.site.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.example.age.common.api.VerifiedUser;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.site.spi.SiteVerifiedAccountStore;
import org.example.age.testing.api.TestModels;
import org.junit.jupiter.api.Test;

public abstract class SiteAccountStoreTestTemplate {

    @Test
    public void verified() {
        VerifiedUser user = TestModels.createVerifiedUser();
        OffsetDateTime expiration = expiration();
        Optional<String> maybeConflictingAccountId = await(store().trySave("username1", user, expiration));
        assertThat(maybeConflictingAccountId).isEmpty();

        VerificationState state = await(store().load("username1"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.getUser()).isEqualTo(user);
        assertThat(state.getExpiration()).isEqualTo(expiration);
    }

    @Test
    public void unverified() {
        VerificationState state = await(store().load("username2"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.UNVERIFIED);
        assertThat(state.getUser()).isNull();
        assertThat(state.getExpiration()).isNull();
    }

    @Test
    public void verified_UpdateAccount() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId1 = await(store().trySave("username3", user, expiration()));
        assertThat(maybeConflictingAccountId1).isEmpty();

        Optional<String> maybeConflictingAccountId2 = await(store().trySave("username3", user, expiration()));
        assertThat(maybeConflictingAccountId2).isEmpty();
    }

    @Test
    public void pseudonymConflict() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId1 = await(store().trySave("username4", user, expiration()));
        assertThat(maybeConflictingAccountId1).isEmpty();

        Optional<String> maybeConflictingAccountId2 = await(store().trySave("username5", user, expiration()));
        assertThat(maybeConflictingAccountId2).hasValue("username4");

        VerificationState state1 = await(store().load("username4"));
        assertThat(state1.getStatus()).isEqualTo(VerificationStatus.VERIFIED);

        VerificationState state2 = await(store().load("username5"));
        assertThat(state2.getStatus()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void expired() throws InterruptedException {
        VerifiedUser user = TestModels.createVerifiedUser();
        OffsetDateTime expiration = expiresIn(Duration.ofMillis(2));
        Optional<String> maybeConflictingAccountId = await(store().trySave("username6", user, expiration));
        assertThat(maybeConflictingAccountId).isEmpty();

        Thread.sleep(4);
        VerificationState state = await(store().load("username6"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.EXPIRED);
        assertThat(state.getUser()).isNull();
        assertThat(state.getExpiration()).isEqualTo(expiration);
    }

    @Test
    public void verified_ExpiredPseudonymConflict() throws InterruptedException {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId1 =
                await(store().trySave("username7", user, expiresIn(Duration.ofMillis(2))));
        assertThat(maybeConflictingAccountId1).isEmpty();

        Thread.sleep(4);
        Optional<String> maybeConflictingAccountId2 = await(store().trySave("username8", user, expiration()));
        assertThat(maybeConflictingAccountId2).isEmpty();

        VerificationState state = await(store().load("username8"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    protected static OffsetDateTime expiration() {
        return expiresIn(Duration.ofMinutes(5));
    }

    protected static OffsetDateTime expiresIn(Duration duration) {
        return OffsetDateTime.now(ZoneOffset.UTC).plus(duration).truncatedTo(ChronoUnit.MILLIS);
    }

    protected abstract SiteVerifiedAccountStore store();
}
