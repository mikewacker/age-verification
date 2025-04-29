package org.example.age.module.store.redis;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.SecureId;
import org.example.age.service.module.store.SiteVerificationStore;
import redis.clients.jedis.AbstractTransaction;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.SetParams;

/** Implementation of {@link SiteVerificationStore} that is backed by Redis. */
@Singleton
final class RedisSiteVerificationStore implements SiteVerificationStore {

    private static final String REDIS_KEY_PREFIX = "age:verification";
    private static final VerificationState UNVERIFIED =
            VerificationState.builder().status(VerificationStatus.UNVERIFIED).build();

    private final JedisPooled client;
    private final RedisUtils utils;

    @Inject
    public RedisSiteVerificationStore(JedisPooled client, RedisUtils utils) {
        this.client = client;
        this.utils = utils;
    }

    @Override
    public CompletionStage<VerificationState> load(String accountId) {
        return utils.runAsync(() -> loadSync(accountId));
    }

    @Override
    public CompletionStage<Optional<String>> trySave(String accountId, VerifiedUser user, OffsetDateTime expiration) {
        return utils.runAsync(() -> trySaveSync(accountId, user, expiration));
    }

    private VerificationState loadSync(String accountId) {
        // Run the Redis commands.
        String redisUserKey = getRedisUserKey(accountId);
        String redisExpirationKey = getRedisExpirationKey(accountId);
        Response<String> userJsonResponse;
        Response<String> rawExpirationResponse;
        try (AbstractTransaction transaction = client.multi()) {
            userJsonResponse = transaction.get(redisUserKey);
            rawExpirationResponse = transaction.get(redisExpirationKey);
            transaction.exec();
        }

        // Process the responses.
        String rawExpiration = rawExpirationResponse.get();
        if (rawExpiration == null) {
            return UNVERIFIED;
        }

        OffsetDateTime expiration = parseTime(rawExpiration);
        String userJson = userJsonResponse.get();
        if (userJson == null) {
            return VerificationState.builder()
                    .status(VerificationStatus.EXPIRED)
                    .expiration(expiration)
                    .build();
        }

        VerifiedUser user = utils.deserialize(userJson, VerifiedUser.class);
        return VerificationState.builder()
                .status(VerificationStatus.VERIFIED)
                .user(user)
                .expiration(expiration)
                .build();
    }

    private Optional<String> trySaveSync(String accountId, VerifiedUser user, OffsetDateTime expiration) {
        // Link the pseudonym to the account, checking for a conflict:
        // SET age:verification:pseudonym:[pseudonym] [accountId] NX PXAT [expiration] GET
        String redisPseudonymKey = getPseudonymKey(user.getPseudonym());
        long pxAt = expiration.toInstant().toEpochMilli();
        String conflictingAccountId =
                client.setGet(redisPseudonymKey, accountId, new SetParams().nx().pxAt(pxAt));
        if (conflictingAccountId != null) {
            return Optional.of(conflictingAccountId);
        }

        // Save the verified account:
        // SET age:verification:account:[accountId]:user [userJson] PXAT [expiration]
        // SET age:verification:account:[accountId]:expiration [expiration]
        // (HSET is not used because we can only expire the entire key, not individual fields.)
        String redisUserKey = getRedisUserKey(accountId);
        String userJson = utils.serialize(user);
        String redisExpirationKey = getRedisExpirationKey(accountId);
        try (AbstractTransaction transaction = client.multi()) {
            transaction.set(redisUserKey, userJson, new SetParams().pxAt(pxAt));
            transaction.set(redisExpirationKey, Long.toString(pxAt));
            transaction.exec();
        }
        return Optional.empty();
    }

    /** Gets the Redis key for an account's user. */
    private String getRedisUserKey(String accountId) {
        String userKey = String.format("account:%s:user", accountId);
        return utils.getRedisKey(REDIS_KEY_PREFIX, userKey);
    }

    /** Gets the Redis key for an account's expiration. */
    private String getRedisExpirationKey(String accountId) {
        String expirationKey = String.format("account:%s:expiration", accountId);
        return utils.getRedisKey(REDIS_KEY_PREFIX, expirationKey);
    }

    /** Gets the Redis key for a pseudonym. */
    private String getPseudonymKey(SecureId pseudonym) {
        String pseudonymKey = String.format("pseudonym:%s", pseudonym);
        return utils.getRedisKey(REDIS_KEY_PREFIX, pseudonymKey);
    }

    /** Parses a time from a raw timestamp. */
    private static OffsetDateTime parseTime(String rawTimestamp) {
        long timestamp = Long.parseLong(rawTimestamp);
        return Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.UTC);
    }
}
