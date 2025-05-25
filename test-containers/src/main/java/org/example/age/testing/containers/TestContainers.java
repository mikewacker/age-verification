package org.example.age.testing.containers;

import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import redis.clients.jedis.JedisPooled;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/** JUnit Jupiter extension for containerized services. Provides clients and cleans state. */
public final class TestContainers implements BeforeAllCallback, AfterAllCallback {

    public static final int REDIS_PORT = 6379;
    public static final int DYNAMODB_PORT = 8000;

    private JedisPooled redis;
    private DynamoDbClient dynamoDb;
    private boolean isInitialized = false;

    /** Gets a Redis client. */
    public JedisPooled redisClient() {
        checkIsInitialized();
        return redis;
    }

    /** Gets a DynamoDB client. */
    public DynamoDbClient dynamoDbClient() {
        checkIsInitialized();
        return dynamoDb;
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        createClients();
        clean();
        isInitialized = true;
    }

    @Override
    public void afterAll(ExtensionContext context) {
        clean();
        closeClients();
    }

    /** Creates all clients. */
    private void createClients() {
        redis = createRedisClient();
        dynamoDb = createDynamoDbClient();
    }

    /** Creates a Redis client. */
    private static JedisPooled createRedisClient() {
        return new JedisPooled("localhost", REDIS_PORT);
    }

    /** Creates a DynamoDB client. */
    private static DynamoDbClient createDynamoDbClient() {
        URI uri = URI.create(String.format("http://localhost:%d", DYNAMODB_PORT));
        AwsCredentialsProvider dummyCredentialsProvider =
                StaticCredentialsProvider.create(AwsBasicCredentials.create("dummyKey", "dummySecret"));
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(uri)
                .credentialsProvider(dummyCredentialsProvider)
                .build();
    }

    /** Closes all clients. */
    private void closeClients() {
        redis.close();
        dynamoDb.close();
    }

    /** Cleans state for all services. */
    private void clean() {
        cleanRedis();
        cleanDynamoDb();
    }

    /** Cleans state for Redis. */
    private void cleanRedis() {
        redis.flushAll();
    }

    /** Cleans state for DynamoDB. */
    private void cleanDynamoDb() {
        List<String> tableNames = dynamoDb.listTables().tableNames();
        for (String tableName : tableNames) {
            dynamoDb.deleteTable(builder -> builder.tableName(tableName));
        }
        for (String tableName : tableNames) {
            dynamoDb.waiter().waitUntilTableNotExists(builder -> builder.tableName(tableName));
        }
    }

    /** Checks that the extension has been initialized. */
    private void checkIsInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException("not initialized (missing @RegisterExtension?)");
        }
    }
}
