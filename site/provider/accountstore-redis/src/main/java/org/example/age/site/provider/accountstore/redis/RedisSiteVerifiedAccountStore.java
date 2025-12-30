package org.example.age.site.provider.accountstore.redis;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.common.env.JsonMapper;
import org.example.age.common.env.Worker;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.site.spi.SiteVerifiedAccountStore;
import redis.clients.jedis.AbstractTransaction;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.SetParams;

/** Implementation of {@link SiteVerifiedAccountStore} that is backed by Redis. */
@Singleton
final class RedisSiteVerifiedAccountStore implements SiteVerifiedAccountStore {

    private static final String REDIS_KEY_PREFIX = "age:verification";

    private final RedisClient client;
    private final JsonMapper mapper;
    private final Worker worker;

    @Inject
    public RedisSiteVerifiedAccountStore(RedisClient client, JsonMapper mapper, Worker worker) {
        this.client = client;
        this.mapper = mapper;
        this.worker = worker;
    }

    @Override
    public CompletionStage<VerificationState> load(String accountId) {
        return worker.dispatch(() -> loadSync(accountId));
    }

    @Override
    public CompletionStage<Optional<String>> trySave(String accountId, VerifiedUser user, OffsetDateTime expiration) {
        return worker.dispatch(() -> trySaveSync(accountId, user, expiration));
    }

    private VerificationState loadSync(String accountId) {
        // Run the Redis commands.
        String redisUserKey = getRedisAccountUserKey(accountId);
        String redisExpirationKey = getRedisAccountExpirationKey(accountId);
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
            return VerificationState.builder()
                    .id(accountId)
                    .status(VerificationStatus.UNVERIFIED)
                    .build();
        }

        OffsetDateTime expiration = parseTime(rawExpiration);
        String userJson = userJsonResponse.get();
        if (userJson == null) {
            return VerificationState.builder()
                    .id(accountId)
                    .status(VerificationStatus.EXPIRED)
                    .expiration(expiration)
                    .build();
        }

        VerifiedUser user = mapper.deserialize(userJson, VerifiedUser.class);
        return VerificationState.builder()
                .id(accountId)
                .status(VerificationStatus.VERIFIED)
                .user(user)
                .expiration(expiration)
                .build();
    }

    private Optional<String> trySaveSync(String accountId, VerifiedUser user, OffsetDateTime expiration) {
        long pxAt = expiration.toInstant().toEpochMilli();
        Optional<String> maybeConflictingAccountId = tryReservePseudonym(accountId, user, pxAt);
        if (maybeConflictingAccountId.isPresent()) {
            return maybeConflictingAccountId;
        }

        // SET {age:verification:account:[accountId]}:user [userJson] PXAT [expiration]
        // SET {age:verification:account:[accountId]}:expiration [expiration]
        // (HSET is not used because we can only expire the entire key, not individual fields.)
        String redisUserKey = getRedisAccountUserKey(accountId);
        String userJson = mapper.serialize(user);
        String redisExpirationKey = getRedisAccountExpirationKey(accountId);
        try (AbstractTransaction transaction = client.multi()) {
            transaction.set(redisUserKey, userJson, new SetParams().pxAt(pxAt));
            transaction.set(redisExpirationKey, Long.toString(pxAt));
            transaction.exec();
        }
        return Optional.empty();
    }

    /** Tries to reserve a pseudonym, returning empty or the account ID that has already reserved this pseudonym. */
    private Optional<String> tryReservePseudonym(String accountId, VerifiedUser user, long pxAt) {
        // SET age:verification:pseudonym:[pseudonym] [accountId] NX PXAT [expiration] GET
        String redisPseudonymKey = getPseudonymKey(user.getPseudonym());
        String conflictingAccountId =
                client.setGet(redisPseudonymKey, accountId, new SetParams().nx().pxAt(pxAt));
        if (conflictingAccountId == null) {
            return Optional.empty();
        }

        if (conflictingAccountId.equals(accountId)) {
            // PEXPIREAT age:verification:pseudonym:[pseudonym] [expiration]
            client.pexpireAt(redisPseudonymKey, pxAt);
            return Optional.empty();
        }

        return Optional.of(conflictingAccountId);
    }

    /** Gets the Redis key for an account's user. */
    private String getRedisAccountUserKey(String accountId) {
        return String.format("{%s:account:%s}:user", REDIS_KEY_PREFIX, accountId);
    }

    /** Gets the Redis key for an account's expiration. */
    private String getRedisAccountExpirationKey(String accountId) {
        return String.format("{%s:account:%s}:expiration", REDIS_KEY_PREFIX, accountId);
    }

    /** Gets the Redis key for a pseudonym. */
    private String getPseudonymKey(SecureId pseudonym) {
        return String.format("%s:pseudonym:%s", REDIS_KEY_PREFIX, pseudonym);
    }

    /** Parses a time from a raw timestamp. */
    private static OffsetDateTime parseTime(String rawTimestamp) {
        long timestamp = Long.parseLong(rawTimestamp);
        return Instant.ofEpochMilli(timestamp).atOffset(ZoneOffset.UTC);
    }
}
