package org.example.age.site.provider.accountstore.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;

import dagger.BindsInstance;
import dagger.Component;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.function.Supplier;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.client.redis.RedisClientConfig;
import org.example.age.site.spi.SiteVerifiedAccountStore;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.env.TestEnvModule;
import org.example.age.testing.site.spi.SiteAccountStoreTestTemplate;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.RedisClient;

public final class RedisSiteAccountStoreTest extends SiteAccountStoreTestTemplate {

    private static final SiteVerifiedAccountStore store = TestComponent.create();

    @Test
    public void redisKeys() {
        VerifiedUser user = TestModels.createVerifiedUser();
        Optional<String> maybeConflictingAccountId = await(store().trySave("username-redis", user, expiration()));
        assertThat(maybeConflictingAccountId).isEmpty();
        try (RedisClient client = RedisClient.create(TestClient.dockerUri("redis", 6379))) {
            String userValue = client.get("{age:verification:account:username-redis}:user");
            assertThat(userValue).isNotNull();
            String expirationValue = client.get("{age:verification:account:username-redis}:expiration");
            assertThat(expirationValue).isNotNull();
            String pseudonymKey = String.format("age:verification:pseudonym:%s", user.getPseudonym());
            String pseudonymValue = client.get(pseudonymKey);
            assertThat(pseudonymValue).isEqualTo("username-redis");
        }
    }

    @Override
    protected SiteVerifiedAccountStore store() {
        return store;
    }

    /** Dagger component for {@link SiteVerifiedAccountStore}. */
    @Component(modules = {RedisSiteAccountStoreModule.class, TestEnvModule.class})
    @Singleton
    interface TestComponent extends Supplier<SiteVerifiedAccountStore> {

        static SiteVerifiedAccountStore create() {
            RedisClientConfig config = RedisClientConfig.builder()
                    .uri(TestClient.dockerUri("redis", 6379))
                    .build();
            return DaggerRedisSiteAccountStoreTest_TestComponent.factory()
                    .create(config)
                    .get();
        }

        @Component.Factory
        interface Factory {

            TestComponent create(@BindsInstance RedisClientConfig config);
        }
    }
}
