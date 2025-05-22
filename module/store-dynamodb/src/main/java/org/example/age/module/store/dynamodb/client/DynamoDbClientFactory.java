package org.example.age.module.store.dynamodb.client;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

/** Factory for a synchronous DynamoDB client. */
final class DynamoDbClientFactory {

    /** Creates a client. */
    public static DynamoDbClient create(DynamoDbConfig config) {
        DynamoDbClientBuilder clientBuilder = DynamoDbClient.builder().region(config.regionAws());
        if (config.testEndpointOverride() != null) {
            AwsCredentialsProvider dummyCredentialsProvider =
                    StaticCredentialsProvider.create(AwsBasicCredentials.create("dummyKey", "dummySecret"));
            clientBuilder.endpointOverride(config.testEndpointOverride()).credentialsProvider(dummyCredentialsProvider);
        }
        return clientBuilder.build();
    }

    // static class
    private DynamoDbClientFactory() {}
}
