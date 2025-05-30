package org.example.age.module.store.dynamodb;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.module.common.EnvUtils;
import org.example.age.service.module.store.AvsVerifiedUserStore;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

/** Implementation of {@link AvsVerifiedUserStore} that is backed by DynamoDB. */
@Singleton
final class DynamoDbAvsVerifiedUserStore implements AvsVerifiedUserStore {

    private static final String USER_TABLE_NAME = "Age.User";

    private final DynamoDbClient client;
    private final EnvUtils utils;

    @Inject
    public DynamoDbAvsVerifiedUserStore(DynamoDbClient client, EnvUtils utils) {
        this.client = client;
        this.utils = utils;
    }

    @Override
    public CompletionStage<Optional<VerifiedUser>> tryLoad(String accountId) {
        return utils.runAsync(() -> loadSync(accountId));
    }

    private Optional<VerifiedUser> loadSync(String accountId) {
        AttributeValue accountIdS = AttributeValue.fromS(accountId);
        GetItemRequest userRequest = GetItemRequest.builder()
                .tableName(USER_TABLE_NAME)
                .key(Map.of("AccountId", accountIdS))
                .attributesToGet("User")
                .build();
        AttributeValue userS = client.getItem(userRequest).item().get("User");
        if (userS == null) {
            return Optional.empty();
        }

        VerifiedUser user = utils.deserialize(userS.s(), VerifiedUser.class);
        return Optional.of(user);
    }
}
