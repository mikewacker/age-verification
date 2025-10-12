package org.example.age.avs.provider.accountstore.dynamodb;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ForbiddenException;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.spi.AvsVerifiedAccountStore;
import org.example.age.avs.spi.VerifiedAccount;
import org.example.age.common.env.JsonMapper;
import org.example.age.common.env.Worker;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;

/** Implementation of {@link AvsVerifiedAccountStore} that is backed by DynamoDB. */
@Singleton
final class DynamoDbAvsVerifiedAccountStore implements AvsVerifiedAccountStore {

    private static final String ACCOUNT_TABLE_NAME = "Age.Account";

    private final DynamoDbClient client;
    private final JsonMapper mapper;
    private final Worker worker;

    @Inject
    public DynamoDbAvsVerifiedAccountStore(DynamoDbClient client, JsonMapper mapper, Worker worker) {
        this.client = client;
        this.mapper = mapper;
        this.worker = worker;
    }

    @Override
    public CompletionStage<VerifiedAccount> load(String accountId) {
        return worker.dispatch(() -> loadSync(accountId));
    }

    private VerifiedAccount loadSync(String accountId) {
        AttributeValue accountIdS = AttributeValue.fromS(accountId);
        GetItemRequest userRequest = GetItemRequest.builder()
                .tableName(ACCOUNT_TABLE_NAME)
                .key(Map.of("AccountId", accountIdS))
                .attributesToGet("Account")
                .build();
        AttributeValue accountS = client.getItem(userRequest).item().get("Account");
        if (accountS == null) {
            throw new ForbiddenException();
        }

        return mapper.deserialize(accountS.s(), VerifiedAccount.class);
    }
}
