package org.example.age.testing.containers;

import org.example.age.api.VerifiedUser;
import org.example.age.testing.JsonTesting;
import redis.clients.jedis.JedisPooled;

/** Test utilities for Redis. */
public final class RedisTestUtils {

    /** Creates an account on the age verification service. */
    public static void createAvsAccount(JedisPooled client, String accountId, VerifiedUser user) {
        String redisKey = String.format("age:user:%s", accountId);
        client.set(redisKey, JsonTesting.serialize(user));
    }

    private RedisTestUtils() {} // static class
}
