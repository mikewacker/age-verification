package org.example.age.module.store.redis.testing;

import org.example.age.api.VerifiedUser;
import org.example.age.common.testing.JsonTesting;
import org.example.age.common.testing.TestClient;
import org.example.age.module.common.testing.BaseTestContainer;
import redis.clients.jedis.JedisPooled;

/** Test container for Redis that has been cleaned. */
public final class RedisTestContainer extends BaseTestContainer<JedisPooled> {

    public static final int PORT = 6379;

    /** Creates an account on the age verification service. */
    public void createAvsAccount(String accountId, VerifiedUser user) {
        String redisKey = String.format("age:user:%s", accountId);
        getClient().set(redisKey, JsonTesting.serialize(user));
    }

    @Override
    protected JedisPooled createClient() {
        return new JedisPooled(TestClient.createLocalhostUri(PORT));
    }

    @Override
    protected void clean(JedisPooled client) {
        client.flushAll();
    }

    @Override
    protected void closeClient(JedisPooled client) {
        client.close();
    }
}
