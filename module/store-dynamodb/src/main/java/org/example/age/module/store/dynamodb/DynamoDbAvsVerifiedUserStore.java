package org.example.age.module.store.dynamodb;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.env.JsonMapper;
import org.example.age.common.env.Worker;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

/** Implementation of {@link AvsVerifiedUserStore} that is backed by DynamoDB. */
@Singleton
final class DynamoDbAvsVerifiedUserStore implements AvsVerifiedUserStore {

    private static final String USER_TABLE_NAME = "Age.User";

    private final DynamoDbClient client;
    private final JsonMapper mapper;
    private final Worker worker;

    @Inject
    public DynamoDbAvsVerifiedUserStore(DynamoDbClient client, JsonMapper mapper, Worker worker) {
        this.client = client;
        this.mapper = mapper;
        this.worker = worker;
    }

    @Override
    public CompletionStage<Optional<VerifiedUser>> tryLoad(String accountId) {
        return worker.dispatch(() -> loadSync(accountId));
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

        VerifiedUser user = mapper.deserialize(userS.s(), VerifiedUser.class);
        return Optional.of(user);
    }
}
