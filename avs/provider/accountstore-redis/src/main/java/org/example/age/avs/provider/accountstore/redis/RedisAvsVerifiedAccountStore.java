package org.example.age.avs.provider.accountstore.redis;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ForbiddenException;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.spi.AvsVerifiedAccountStore;
import org.example.age.avs.spi.VerifiedAccount;
import org.example.age.common.env.JsonMapper;
import org.example.age.common.env.Worker;
import redis.clients.jedis.RedisClient;

/** Implementation of {@link AvsVerifiedAccountStore} that is backed by Redis. */
@Singleton
final class RedisAvsVerifiedAccountStore implements AvsVerifiedAccountStore {

    private static final String REDIS_KEY_PREFIX = "age:account";

    private final RedisClient client;
    private final JsonMapper mapper;
    private final Worker worker;

    @Inject
    public RedisAvsVerifiedAccountStore(RedisClient client, JsonMapper mapper, Worker worker) {
        this.client = client;
        this.mapper = mapper;
        this.worker = worker;
    }

    @Override
    public CompletionStage<VerifiedAccount> load(String accountId) {
        return worker.dispatch(() -> loadSync(accountId));
    }

    private VerifiedAccount loadSync(String accountId) {
        String redisKey = String.format("%s:%s", REDIS_KEY_PREFIX, accountId);
        String accountJson = client.get(redisKey);
        if (accountJson == null) {
            throw new ForbiddenException();
        }

        return mapper.deserialize(accountJson, VerifiedAccount.class);
    }
}
