package org.example.age.module.store.dynamodb;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerificationState;
import org.example.age.api.VerificationStatus;
import org.example.age.api.VerifiedUser;
import org.example.age.module.common.EnvUtils;
import org.example.age.service.module.store.SiteVerificationStore;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

/** Implementation of {@link SiteVerificationStore} that is backed by DynamoDB. */
@Singleton
final class DynamoDbSiteVerificationStore implements SiteVerificationStore {

    private static final String ACCOUNT_TABLE_NAME = "Age.Verification.Account";
    private static final String PSEUDONYM_TABLE_NAME = "Age.Verification.Pseudonym";
    private static final VerificationState UNVERIFIED =
            VerificationState.builder().status(VerificationStatus.UNVERIFIED).build();

    private final DynamoDbClient client;
    private final EnvUtils utils;

    @Inject
    public DynamoDbSiteVerificationStore(DynamoDbClient client, EnvUtils utils) {
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
        AttributeValue accountIdS = AttributeValue.fromS(accountId);
        GetItemRequest accountRequest = GetItemRequest.builder()
                .tableName(ACCOUNT_TABLE_NAME)
                .key(Map.of("AccountId", accountIdS))
                .attributesToGet("State")
                .build();
        AttributeValue stateS = client.getItem(accountRequest).item().get("State");
        if (stateS == null) {
            return UNVERIFIED;
        }

        VerificationState state = utils.deserialize(stateS.s(), VerificationState.class);
        if (state.getExpiration().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            return VerificationState.builder()
                    .status(VerificationStatus.EXPIRED)
                    .expiration(state.getExpiration())
                    .build();
        }

        return state;
    }

    private Optional<String> trySaveSync(String accountId, VerifiedUser user, OffsetDateTime expiration) {
        AttributeValue accountIdS = AttributeValue.fromS(accountId);
        Optional<String> maybeConflictingAccountId = tryReservePseudonym(accountIdS, user, expiration);
        if (maybeConflictingAccountId.isPresent()) {
            return maybeConflictingAccountId;
        }

        VerificationState state = VerificationState.builder()
                .status(VerificationStatus.VERIFIED)
                .user(user)
                .expiration(expiration)
                .build();
        AttributeValue stateS = AttributeValue.fromS(utils.serialize(state));
        PutItemRequest accountRequest = PutItemRequest.builder()
                .tableName(ACCOUNT_TABLE_NAME)
                .item(Map.of("AccountId", accountIdS, "State", stateS))
                .build();
        client.putItem(accountRequest);
        return Optional.empty();
    }

    /** Tries to reserve a pseudonym, returning empty or the account ID that has already reserved this pseudonym. */
    private Optional<String> tryReservePseudonym(
            AttributeValue accountIdS, VerifiedUser user, OffsetDateTime expiration) {
        AttributeValue pseudonymS = AttributeValue.fromS(user.getPseudonym().toString());
        AttributeValue expirationN =
                AttributeValue.fromN(Long.toString(expiration.toInstant().toEpochMilli()));
        AttributeValue nowN = AttributeValue.fromN(Long.toString(System.currentTimeMillis()));
        PutItemRequest pseudonymRequest = PutItemRequest.builder()
                .tableName(PSEUDONYM_TABLE_NAME)
                .item(Map.of("Pseudonym", pseudonymS, "AccountId", accountIdS, "Expiration", expirationN))
                .conditionExpression("attribute_not_exists(#Key) OR (#Value = :AccountId) OR (#TTL <= :Now)")
                .expressionAttributeNames(Map.of("#Key", "Pseudonym", "#Value", "AccountId", "#TTL", "Expiration"))
                .expressionAttributeValues(Map.of(":AccountId", accountIdS, ":Now", nowN))
                .build();
        try {
            client.putItem(pseudonymRequest);
            return Optional.empty();
        } catch (ConditionalCheckFailedException e) {
            GetItemRequest getConflictRequest = GetItemRequest.builder()
                    .tableName(PSEUDONYM_TABLE_NAME)
                    .key(Map.of("Pseudonym", pseudonymS))
                    .attributesToGet("AccountId")
                    .build();
            AttributeValue conflictingAccountId =
                    client.getItem(getConflictRequest).item().get("AccountId");
            return Optional.of((conflictingAccountId != null) ? conflictingAccountId.s() : "???");
        }
    }
}
