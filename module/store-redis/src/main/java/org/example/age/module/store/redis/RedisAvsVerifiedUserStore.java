package org.example.age.module.store.redis;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.common.env.JsonMapper;
import org.example.age.common.env.Worker;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import redis.clients.jedis.JedisPooled;

/** Implementation of {@link AvsVerifiedUserStore} that is backed by Redis. */
@Singleton
final class RedisAvsVerifiedUserStore implements AvsVerifiedUserStore {

    private static final String REDIS_KEY_PREFIX = "age:user";

    private final JedisPooled client;
    private final JsonMapper mapper;
    private final Worker worker;

    @Inject
    public RedisAvsVerifiedUserStore(JedisPooled client, JsonMapper mapper, Worker worker) {
        this.client = client;
        this.mapper = mapper;
        this.worker = worker;
    }

    @Override
    public CompletionStage<Optional<VerifiedUser>> tryLoad(String accountId) {
        return worker.dispatch(() -> tryLoadSync(accountId));
    }

    private Optional<VerifiedUser> tryLoadSync(String accountId) {
        String redisKey = String.format("%s:%s", REDIS_KEY_PREFIX, accountId);
        String json = client.get(redisKey);
        return (json != null) ? Optional.of(mapper.deserialize(json, VerifiedUser.class)) : Optional.empty();
    }
}
