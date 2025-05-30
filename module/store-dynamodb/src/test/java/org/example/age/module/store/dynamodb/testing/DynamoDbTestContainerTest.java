package org.example.age.module.store.dynamodb.testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public final class DynamoDbTestContainerTest {

    @RegisterExtension
    private static final DynamoDbTestContainer dynamoDb = new DynamoDbTestContainer();

    @Test
    public void setUpContainer() {
        dynamoDb.createSiteAccountStoreTables();
    }
}
