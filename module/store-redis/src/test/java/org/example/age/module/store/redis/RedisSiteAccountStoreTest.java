package org.example.age.module.store.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.WebStageTesting.await;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.VerifiedUser;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.example.age.service.module.store.SiteVerificationStore;
import org.example.age.testing.TestModels;
import org.example.age.testing.containers.TestContainers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import redis.clients.jedis.JedisPooled;

public final class RedisSiteAccountStoreTest {

    @RegisterExtension
    private static final TestContainers containers = new TestContainers();

    private static SiteVerificationStore store;

    @BeforeAll
    public static void createSiteVerificationStore() {
        TestComponent component = TestComponent.create();
        store = component.siteVerificationStore();
    }

    @Test
    public void saveThenLoad() {
        VerifiedUser user = TestModels.createVerifiedUser();
        OffsetDateTime expiration = expiresIn(300000);
        Optional<String> maybeConflictingAccountId = await(store.trySave("username1", user, expiration));
        assertThat(maybeConflictingAccountId).isEmpty();

        VerificationState state = await(store.load("username1"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.getUser()).isEqualTo(user);
        assertThat(state.getExpiration()).isEqualTo(expiration);
    }

    @Test
    public void load() {
        VerificationState state = await(store.load("username2"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.UNVERIFIED);
        assertThat(state.getUser()).isNull();
        assertThat(state.getExpiration()).isNull();
    }

    @Test
    public void saveTwice() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId1 = await(store.trySave("username3", user, expiresIn(300000)));
        assertThat(maybeConflictingAccountId1).isEmpty();

        Optional<String> maybeConflictingAccountId2 = await(store.trySave("username3", user, expiresIn(300000)));
        assertThat(maybeConflictingAccountId2).isEmpty();
    }

    @Test
    public void saveFails_Conflict() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId1 = await(store.trySave("username4", user, expiresIn(300000)));
        assertThat(maybeConflictingAccountId1).isEmpty();

        Optional<String> maybeConflictingAccountId2 = await(store.trySave("username5", user, expiresIn(300000)));
        assertThat(maybeConflictingAccountId2).hasValue("username4");

        VerificationState state1 = await(store.load("username4"));
        assertThat(state1.getStatus()).isEqualTo(VerificationStatus.VERIFIED);

        VerificationState state2 = await(store.load("username5"));
        assertThat(state2.getStatus()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void saveThenExpireThenLoad() throws InterruptedException {
        VerifiedUser user = TestModels.createVerifiedUser();
        OffsetDateTime expiration = expiresIn(2);
        Optional<String> maybeConflictingAccountId = await(store.trySave("username6", user, expiration));
        assertThat(maybeConflictingAccountId).isEmpty();

        Thread.sleep(4);
        VerificationState state = await(store.load("username6"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.EXPIRED);
        assertThat(state.getUser()).isNull();
        assertThat(state.getExpiration()).isEqualTo(expiration);
    }

    @Test
    public void save_ExpiredConflict() throws InterruptedException {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId1 = await(store.trySave("username7", user, expiresIn(2)));
        assertThat(maybeConflictingAccountId1).isEmpty();

        Thread.sleep(4);
        Optional<String> maybeConflictingAccountId2 = await(store.trySave("username8", user, expiresIn(300000)));
        assertThat(maybeConflictingAccountId2).isEmpty();

        VerificationState state = await(store.load("username8"));
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    public void saveThenGetFromRedis() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId = await(store.trySave("username9", user, expiresIn(300000)));
        assertThat(maybeConflictingAccountId).isEmpty();

        JedisPooled client = containers.redisClient();
        String userValue = client.get("{age:verification:account:username9}:user");
        assertThat(userValue).isNotNull();
        String expirationValue = client.get("{age:verification:account:username9}:expiration");
        assertThat(expirationValue).isNotNull();
        String pseudonymKey = String.format("age:verification:pseudonym:%s", user.getPseudonym());
        String pseudonymValue = client.get(pseudonymKey);
        assertThat(pseudonymValue).isEqualTo("username9");
    }

    private static OffsetDateTime expiresIn(int ms) {
        return OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMillis(ms)).truncatedTo(ChronoUnit.MILLIS);
    }

    /** Dagger component for the stores. */
    @Component(modules = {RedisSiteAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerRedisSiteAccountStoreTest_TestComponent.create();
        }

        SiteVerificationStore siteVerificationStore();
    }
}
