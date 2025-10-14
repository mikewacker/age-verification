package org.example.age.demo;

import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.api.VerificationRequest;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.client.SiteApi;
import org.example.age.testing.client.TestClient;
import org.example.age.testing.json.TestObjectMapper;
import retrofit2.Response;

/** Runs the demo. */
public final class Demo {

    private static final AvsApi parentAvsClient = createClient(8082, "John Smith", AvsApi.class);
    private static final AvsApi childAvsClient = createClient(8082, "Billy Smith", AvsApi.class);
    private static final SiteApi parentCrackleClient = createClient(8080, "publius", SiteApi.class);
    private static final SiteApi childCrackleClient = createClient(8080, "publius-jr", SiteApi.class);
    private static final SiteApi parentPopClient = createClient(8081, "JohnS", SiteApi.class);
    private static final SiteApi childPopClient = createClient(8081, "BillyS", SiteApi.class);

    private static final String AVS_NAME = "CheckMyAge";

    private static final ObjectWriter writer = TestObjectMapper.get().writerWithDefaultPrettyPrinter();

    /** Main method. */
    @SuppressWarnings("CatchAndPrintStackTrace")
    public static void main(String[] args) {
        try {
            verifyAge(parentCrackleClient, parentAvsClient, "Crackle", "publius", "John Smith", true);
            verifyAge(childCrackleClient, childAvsClient, "Crackle", "publius-jr", "Billy Smith", false);
            verifyAge(parentPopClient, parentAvsClient, "Pop", "JohnS", "John Smith", false);
            verifyAge(childPopClient, childAvsClient, "Pop", "BillyS", "Billy Smith", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Verifies a single account. */
    private static void verifyAge(
            SiteApi siteClient, AvsApi avsClient, String siteName, String accountName, String realName, boolean verbose)
            throws IOException {
        log("\n================================================================\n");
        log("%s uses %s to verify his account on %s, \"%s\".", realName, AVS_NAME, siteName, accountName);

        VerificationRequest request = get(siteClient.createVerificationRequest().execute());
        if (verbose) {
            log("- On %s, %s begins the process to verify \"%s\".", siteName, realName, accountName);
            log("- (Behind the scenes, %s contacts %s.)", siteName, AVS_NAME);
            log("    - %s does NOT share the account name, \"%s\", with %s.", siteName, accountName, AVS_NAME);
            log("- %s redirects %s to %s.", siteName, realName, AVS_NAME);
        }

        get(avsClient.linkVerificationRequest(request.getId()).execute());
        get(avsClient.sendAgeCertificate().execute());
        if (verbose) {
            log("- %s confirms with %s that he wants to verify an account on %s.", realName, AVS_NAME, siteName);
            log("    - %s does NOT know which account on %s is being verified.", AVS_NAME, siteName);
            log("- (Behind the scenes, %s sends an age certificate to %s.)", AVS_NAME, siteName);
            log("    - %s does NOT share %s's real name with %s.", AVS_NAME, realName, siteName);
            log("- %s redirects %s to %s.", AVS_NAME, realName, siteName);
        }

        VerificationState state = get(siteClient.getVerificationState().execute());
        log("\"%s\" is verified on %s:", accountName, siteName);
        logJson(state.getUser());
    }

    /** Creates a client for an account. */
    private static <A> A createClient(int port, String accountId, Class<A> apiType) {
        return TestClient.api(port, requestBuilder -> requestBuilder.header("Account-Id", accountId), apiType);
    }

    /** Gets the result of a successful response. */
    private static <V> V get(Response<V> response) {
        if (!response.isSuccessful()) {
            String message = String.format("request failed with %d", response.code());
            throw new IllegalStateException(message);
        }

        return response.body();
    }

    /** Logs a message. */
    @SuppressWarnings("AnnotateFormatMethod")
    public static void log(String format, Object... args) {
        String msg = String.format(format, args);
        System.out.println(msg);
    }

    /** Logs a value as pretty-printed JSON. */
    public static void logJson(Object value) throws IOException {
        String json = writer.writeValueAsString(value);
        System.out.println(json);
    }

    private Demo() {} // static class
}
