package org.example.age.module.store.redis;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.service.module.env.EnvUtils;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import redis.clients.jedis.JedisPooled;

/** Implementation of {@link AvsVerifiedUserStore} that is backed by Redis. */
@Singleton
final class RedisAvsVerifiedUserStore implements AvsVerifiedUserStore {

    private static final String REDIS_KEY_PREFIX = "age:user";

    private final JedisPooled client;
    private final EnvUtils utils;

    @Inject
    public RedisAvsVerifiedUserStore(JedisPooled client, EnvUtils utils) {
        this.client = client;
        this.utils = utils;
    }

    @Override
    public CompletionStage<Optional<VerifiedUser>> tryLoad(String accountId) {
        return utils.runAsync(() -> tryLoadSync(accountId));
    }

    private Optional<VerifiedUser> tryLoadSync(String accountId) {
        String redisKey = String.format("%s:%s", REDIS_KEY_PREFIX, accountId);
        String json = client.get(redisKey);
        return (json != null) ? Optional.of(utils.deserialize(json, VerifiedUser.class)) : Optional.empty();
    }
}
