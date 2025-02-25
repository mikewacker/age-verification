package org.example.age.demo;

import com.google.errorprone.annotations.FormatMethod;
import java.io.IOException;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.client.AvsApi;
import org.example.age.api.client.SiteApi;
import org.example.age.app.AvsApp;
import org.example.age.app.SiteApp;
import retrofit2.Response;

/** Runs the demo. */
public final class Demo {

    private static final AvsApp checkMyAge = new AvsApp("check-my-age");
    private static final SiteApp crackle = new SiteApp("crackle");
    private static final SiteApp pop = new SiteApp("pop");

    private static final AvsApi parentAvsClient = DemoInfra.createClient(9090, "John Smith", AvsApi.class);
    private static final AvsApi childAvsClient = DemoInfra.createClient(9090, "Billy Smith", AvsApi.class);
    private static final SiteApi parentCrackleClient = DemoInfra.createClient(8080, "publius", SiteApi.class);
    private static final SiteApi childCrackleClient = DemoInfra.createClient(8080, "publius-jr", SiteApi.class);
    private static final SiteApi parentPopClient = DemoInfra.createClient(8081, "JohnS", SiteApi.class);
    private static final SiteApi childPopClient = DemoInfra.createClient(8081, "BillyS", SiteApi.class);

    private static final String AVS_NAME = "CheckMyAge";

    /** Main method. */
    @SuppressWarnings("CatchAndPrintStackTrace")
    public static void main(String[] args) throws IOException {
        try {
            startServers();
            verifyAge(parentCrackleClient, parentAvsClient, "Crackle", "publius", "John Smith", true);
            verifyAge(childCrackleClient, childAvsClient, "Crackle", "publius-jr", "Billy Smith", false);
            verifyAge(parentPopClient, parentAvsClient, "Pop", "JohnS", "John Smith", false);
            verifyAge(childPopClient, childAvsClient, "Pop", "BillyS", "Billy Smith", false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DemoInfra.stop();
        }
    }

    /** Starts all servers. */
    private static void startServers() throws Exception {
        DemoInfra.startRedis();
        DemoInfra.populateRedis();
        DemoInfra.startServer(checkMyAge, "config-check-my-age.yaml");
        DemoInfra.startServer(crackle, "config-crackle.yaml");
        DemoInfra.startServer(pop, "config-pop.yaml");
    }

    /** Verifies a single account. */
    private static void verifyAge(
            SiteApi siteClient, AvsApi avsClient, String siteName, String accountName, String realName, boolean verbose)
            throws IOException {
        log(true, "\n================================================================\n");
        log(true, "%s uses %s to verify his account on %s, \"%s\".", realName, AVS_NAME, siteName, accountName);

        VerificationRequest request = get(siteClient.createVerificationRequest().execute());
        log(verbose, "- On %s, %s begins the process to verify \"%s\".", siteName, realName, accountName);
        log(verbose, "- (Behind the scenes, %s contacts %s.)", siteName, AVS_NAME);
        log(verbose, "    - %s does NOT share the account name, \"%s\", with %s.", siteName, accountName, AVS_NAME);
        log(verbose, "- %s redirects %s to %s.", siteName, realName, AVS_NAME);

        get(avsClient.linkVerificationRequest(request.getId()).execute());
        get(avsClient.sendAgeCertificate().execute());
        log(verbose, "- %s confirms with %s that he wants to verify an account on %s.", realName, AVS_NAME, siteName);
        log(verbose, "    - %s does NOT know which account on %s is being verified.", AVS_NAME, siteName);
        log(verbose, "- (Behind the scenes, %s sends an age certificate to %s.)", AVS_NAME, siteName);
        log(verbose, "    - %s does NOT share %s's real name with %s.", AVS_NAME, realName, siteName);
        log(verbose, "- %s redirects %s to %s.", AVS_NAME, realName, siteName);

        VerificationState state = get(siteClient.getVerificationState().execute());
        log(true, "\"%s\" is verified on %s:", accountName, siteName);
        logJson(state.getUser());
    }

    /** Gets the result of a successful response. */
    private static <V> V get(Response<V> response) {
        if (!response.isSuccessful()) {
            String message = String.format("request failed with %d", response.code());
            throw new IllegalStateException(message);
        }

        return response.body();
    }

    /** Logs a message to the console. */
    @FormatMethod
    private static void log(boolean show, String format, Object... args) {
        if (!show) {
            return;
        }

        String msg = String.format(format, args);
        System.out.println(msg);
    }

    /** Logs a value as JSON to the console. */
    private static void logJson(Object value) throws IOException {
        String json = DemoInfra.getObjectWriter().writeValueAsString(value);
        System.out.println(json);
    }

    // static class
    private Demo() {}
}
