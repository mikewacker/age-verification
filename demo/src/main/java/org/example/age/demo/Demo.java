package org.example.age.demo;

import java.util.List;
import org.example.age.api.AgeRange;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.VerifiedUser;
import org.example.age.api.client.AvsApi;
import org.example.age.api.client.SiteApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.app.AvsApp;
import org.example.age.app.SiteApp;
import org.example.age.module.store.dynamodb.testing.DynamoDbTestContainer;
import org.example.age.module.store.redis.testing.RedisTestContainer;
import org.example.age.testing.util.TestClient;
import retrofit2.Response;

/** Runs the demo. */
public final class Demo {

    private static final AvsApp checkMyAge = new AvsApp("check-my-age");
    private static final SiteApp crackle = new SiteApp("crackle");
    private static final SiteApp pop = new SiteApp("pop");
    private static final RedisTestContainer redis = new RedisTestContainer();
    private static final DynamoDbTestContainer dynamoDb = new DynamoDbTestContainer();

    private static final AvsApi parentAvsClient = createClient(9090, "John Smith", AvsApi.class);
    private static final AvsApi childAvsClient = createClient(9090, "Billy Smith", AvsApi.class);
    private static final SiteApi parentCrackleClient = createClient(8080, "publius", SiteApi.class);
    private static final SiteApi childCrackleClient = createClient(8080, "publius-jr", SiteApi.class);
    private static final SiteApi parentPopClient = createClient(8081, "JohnS", SiteApi.class);
    private static final SiteApi childPopClient = createClient(8081, "BillyS", SiteApi.class);

    private static final String AVS_NAME = "CheckMyAge";

    /** Main method. */
    @SuppressWarnings("CatchAndPrintStackTrace")
    public static void main(String[] args) throws Exception {
        setUp();
        try {
            Logger.setVerbose(true);
            verifyAge(parentCrackleClient, parentAvsClient, "Crackle", "publius", "John Smith");
            Logger.setVerbose(false);
            verifyAge(childCrackleClient, childAvsClient, "Crackle", "publius-jr", "Billy Smith");
            verifyAge(parentPopClient, parentAvsClient, "Pop", "JohnS", "John Smith");
            verifyAge(childPopClient, childAvsClient, "Pop", "BillyS", "Billy Smith");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            tearDown();
        }
    }

    /** Verifies a single account. */
    private static void verifyAge(
            SiteApi siteClient, AvsApi avsClient, String siteName, String accountName, String realName)
            throws Exception {
        Logger.info("\n================================================================\n");
        Logger.info("%s uses %s to verify his account on %s, \"%s\".", realName, AVS_NAME, siteName, accountName);

        VerificationRequest request = get(siteClient.createVerificationRequest().execute());
        Logger.verbose("- On %s, %s begins the process to verify \"%s\".", siteName, realName, accountName);
        Logger.verbose("- (Behind the scenes, %s contacts %s.)", siteName, AVS_NAME);
        Logger.verbose("    - %s does NOT share the account name, \"%s\", with %s.", siteName, accountName, AVS_NAME);
        Logger.verbose("- %s redirects %s to %s.", siteName, realName, AVS_NAME);

        get(avsClient.linkVerificationRequest(request.getId()).execute());
        get(avsClient.sendAgeCertificate().execute());
        Logger.verbose("- %s confirms with %s that he wants to verify an account on %s.", realName, AVS_NAME, siteName);
        Logger.verbose("    - %s does NOT know which account on %s is being verified.", AVS_NAME, siteName);
        Logger.verbose("- (Behind the scenes, %s sends an age certificate to %s.)", AVS_NAME, siteName);
        Logger.verbose("    - %s does NOT share %s's real name with %s.", AVS_NAME, realName, siteName);
        Logger.verbose("- %s redirects %s to %s.", AVS_NAME, realName, siteName);

        VerificationState state = get(siteClient.getVerificationState().execute());
        Logger.info("\"%s\" is verified on %s:", accountName, siteName);
        Logger.json(state.getUser());
    }

    /** Gets the result of a successful response. */
    private static <V> V get(Response<V> response) {
        if (!response.isSuccessful()) {
            String message = String.format("request failed with %d", response.code());
            throw new IllegalStateException(message);
        }

        return response.body();
    }

    /** Creates a client for an account. */
    private static <A> A createClient(int port, String accountId, Class<A> apiType) {
        return TestClient.api(port, requestBuilder -> requestBuilder.header("Account-Id", accountId), apiType);
    }

    /** Sets up the demo. */
    private static void setUp() throws Exception {
        // Set up containers.
        redis.beforeAll(null);
        dynamoDb.beforeAll(null);
        dynamoDb.createSiteAccountStoreTables(); // can share since each site has a different pseudonym for a person
        dynamoDb.createAvsAccountStoreTables();
        SecureId parentPseudonym = SecureId.fromString("uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4");
        VerifiedUser parent = VerifiedUser.builder()
                .pseudonym(parentPseudonym)
                .ageRange(AgeRange.builder().min(40).max(40).build())
                .build();
        dynamoDb.createAvsAccount("John Smith", parent);
        SecureId childPseudonym = SecureId.fromString("KB0b9pDo8j7-1p90fFokbgHj8hzbbU7jCGGjfuMzLR4");
        VerifiedUser child = VerifiedUser.builder()
                .pseudonym(childPseudonym)
                .ageRange(AgeRange.builder().min(13).max(13).build())
                .guardianPseudonyms(List.of(parentPseudonym))
                .build();
        dynamoDb.createAvsAccount("Billy Smith", child);

        // Start servers.
        checkMyAge.run("server", Resources.get("config-check-my-age.yaml"));
        crackle.run("server", Resources.get("config-crackle.yaml"));
        pop.run("server", Resources.get("config-pop.yaml"));
    }

    /** Tears down the demo. */
    private static void tearDown() throws Exception {
        redis.afterAll(null);
        dynamoDb.afterAll(null);
        System.exit(0);
    }

    private Demo() {} // static class
}
