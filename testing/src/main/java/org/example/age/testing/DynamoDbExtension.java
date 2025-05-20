package org.example.age.testing;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import java.net.URI;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/** DynamoDB client and server for testing. */
public final class DynamoDbExtension extends ClientServerExtension<DynamoDbClient, DynamoDBProxyServer> {

    /** Creates a client and a server that is bound to any available port. */
    public DynamoDbExtension() {
        super();
    }

    /** Creates a client and a server that is bound to the provided port. */
    public DynamoDbExtension(int port) {
        super(port);
    }

    @Override
    protected DynamoDBProxyServer startServer(int port) throws Exception {
        String[] args = {"-inMemory", "-port", Integer.toString(port)};
        DynamoDBProxyServer server = ServerRunner.createServerFromCommandLineArgs(args);
        server.start();
        return server;
    }

    @Override
    protected void stopServer(DynamoDBProxyServer server) throws Exception {
        server.stop();
    }

    @Override
    protected DynamoDbClient createClient(int port) {
        URI uri = URI.create(String.format("http://localhost:%d", port));
        AwsCredentialsProvider dummyCredentialsProvider =
                StaticCredentialsProvider.create(AwsBasicCredentials.create("dummyKey", "dummySecret"));
        return DynamoDbClient.builder()
                .endpointOverride(uri)
                .region(Region.US_EAST_1)
                .credentialsProvider(dummyCredentialsProvider)
                .build();
    }

    @Override
    protected void closeClient(DynamoDbClient client) {
        client.close();
    }
}
