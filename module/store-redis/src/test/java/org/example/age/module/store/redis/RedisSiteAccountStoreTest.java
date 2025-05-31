package org.example.age.module.store.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.example.age.api.VerifiedUser;
import org.example.age.api.testing.TestModels;
import org.example.age.module.store.redis.testing.RedisTestContainer;
import org.example.age.module.store.redis.testing.TestDependenciesModule;
import org.example.age.service.module.store.SiteVerificationStore;
import org.example.age.service.module.store.testing.SiteAccountStoreTestTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import redis.clients.jedis.JedisPooled;

public final class RedisSiteAccountStoreTest extends SiteAccountStoreTestTemplate {

    @RegisterExtension
    private static final RedisTestContainer redis = new RedisTestContainer();

    private static SiteVerificationStore store;

    @BeforeAll
    public static void createSiteVerificationStore() {
        TestComponent component = TestComponent.create();
        store = component.siteVerificationStore();
    }

    @Test
    public void redisKeys() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId = await(store().trySave("username-redis", user, expiration()));
        assertThat(maybeConflictingAccountId).isEmpty();

        JedisPooled client = redis.getClient();
        String userValue = client.get("{age:verification:account:username-redis}:user");
        assertThat(userValue).isNotNull();
        String expirationValue = client.get("{age:verification:account:username-redis}:expiration");
        assertThat(expirationValue).isNotNull();
        String pseudonymKey = String.format("age:verification:pseudonym:%s", user.getPseudonym());
        String pseudonymValue = client.get(pseudonymKey);
        assertThat(pseudonymValue).isEqualTo("username-redis");
    }

    @Override
    protected SiteVerificationStore store() {
        return store;
    }

    /** Dagger component for the store. */
    @Component(modules = {RedisSiteAccountStoreModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerRedisSiteAccountStoreTest_TestComponent.create();
        }

        SiteVerificationStore siteVerificationStore();
    }
}
