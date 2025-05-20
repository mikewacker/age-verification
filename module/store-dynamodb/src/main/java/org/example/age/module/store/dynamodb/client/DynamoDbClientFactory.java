package org.example.age.module.store.dynamodb.client;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClientBuilder;

/** Factory for an asynchronous DynamoDB client. */
final class DynamoDbClientFactory {

    /** Creates a client. */
    public static DynamoDbAsyncClient create(DynamoDbConfig config) {
        DynamoDbAsyncClientBuilder clientBuilder = DynamoDbAsyncClient.builder().region(config.regionAws());
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
