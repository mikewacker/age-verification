package org.example.age.demo;

import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.errorprone.annotations.FormatMethod;
import io.github.mikewacker.drift.api.HttpOptional;
import io.github.mikewacker.drift.client.JsonApiClient;
import io.undertow.Undertow;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.example.age.api.def.VerificationState;
import org.example.age.api.def.VerificationStatus;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.demo.server.DemoAvsServerComponent;
import org.example.age.demo.server.DemoSiteServerComponent;

/** Demos a proof-of-concept for age verification. */
public final class Main {

    private static final String SITE1_NAME = "Crackle";
    private static final String SITE2_NAME = "Pop";
    private static final String AVS_NAME = "CheckMyAge";
    private static final int SITE1_PORT = 8080;
    private static final int SITE2_PORT = 8081;
    private static final int AVS_PORT = 8090;

    private static final Undertow siteServer1 = DemoSiteServerComponent.createServer(SITE1_NAME);
    private static final Undertow siteServer2 = DemoSiteServerComponent.createServer(SITE2_NAME);
    private static final Undertow avsServer = DemoAvsServerComponent.createServer(AVS_NAME);

    private static final ObjectWriter jsonWriter =
            new ObjectMapper().setBase64Variant(Base64Variants.MODIFIED_FOR_URL).writerWithDefaultPrettyPrinter();

    /** Main method. */
    public static void main(String[] args) throws IOException {
        try {
            startServers();
            displayIntro();
            verifyAccounts();
            duplicateVerification();
            displayUserData();
        } finally {
            stopServers();
        }
    }

    /** Displays some introductory text. */
    private static void displayIntro() {
        displayBorder();
        println("- Crackle and Pop are social media sites.");
        println("- CheckMyAge is a third-party age verification service.");
        println("- John Smith (40) and Billy Smith (13, son of John) have accounts on CheckMyAge.");
        println();
        displayBorder();
    }

    ////////
    // the basics
    ////////

    /** Verifies accounts. */
    private static void verifyAccounts() throws IOException {
        verifyAccount(SITE1_NAME, SITE1_PORT, "publius", "John Smith", true);
        verifyAccount(SITE1_NAME, SITE1_PORT, "publius-jr", "Billy Smith", false);
        verifyAccount(SITE2_NAME, SITE2_PORT, "JohnS", "John Smith", false);
        verifyAccount(SITE2_NAME, SITE2_PORT, "BillyS", "Billy Smith", false);
        displayBorder();
    }

    /** Verifies an account on a site. */
    private static void verifyAccount(
            String siteName, int sitePort, String siteAccountId, String person, boolean verbose) throws IOException {
        println("%s uses %s to verify his account on %s, \"%s\":", person, AVS_NAME, siteName, siteAccountId);
        println("- On %s, %s begins the process to verify \"%s\".", siteName, person, siteAccountId);
        println("- (Behind the scenes, %s contacts %s.)", siteName, AVS_NAME);
        println("    - %s does NOT share the account name, \"%s\", with %s.", siteName, siteAccountId, AVS_NAME);
        String requestUrl = verificationRequestUrl(sitePort);
        VerificationRequest request = createVerificationRequest(requestUrl, siteAccountId);
        if (verbose) {
            displayExchange("POST", requestUrl, request);
        }

        println("- %s redirects %s to CheckMyAge.", siteName, person);
        linkVerificationRequest(request.redirectUrl(), person);
        if (verbose) {
            displayExchange("POST", request.redirectUrl(), 200);
        }

        println("- %s confirms with %s that he wants to verify an account on %s.", person, AVS_NAME, siteName);
        println("    - %s does NOT know which account on %s is being verified.", AVS_NAME, siteName);
        println("- (Behind the scenes, %s sends an age certificate to %s.)", AVS_NAME, siteName);
        println("    - %s does NOT share %s's real identity with %s.", AVS_NAME, person, siteName);
        String certificateUrl = ageCertificateUrl();
        HttpOptional<String> maybeRedirectUrl = sendAgeCertificate(certificateUrl, person);
        if (maybeRedirectUrl.isEmpty()) {
            error();
        }
        String redirectUrl = maybeRedirectUrl.get();
        if (verbose) {
            displayExchange("POST", certificateUrl, redirectUrl);
        }

        println("- %s redirects %s back to %s.", AVS_NAME, person, siteName);
        println("- %s confirms that \"%s\" is verified!", siteName, siteAccountId);
        VerificationState state = getVerificationState(redirectUrl, siteAccountId);
        if (state.status() != VerificationStatus.VERIFIED) {
            error();
        }
        if (verbose) {
            displayExchange("GET", redirectUrl, state);
        }

        println("- %s has stored the following data for \"%s\":", siteName, siteAccountId);
        displayVerifiedUser(state.verifiedUser());
        println();
    }

    /** Displays a verified user. */
    private static void displayVerifiedUser(VerifiedUser verifiedUser) {
        println("    - Pseudonym: %s", verifiedUser.pseudonym());
        println("    - Age: %s", verifiedUser.ageRange());
        if (!verifiedUser.guardianPseudonyms().isEmpty()) {
            SecureId guardianPseudonym = verifiedUser.guardianPseudonyms().get(0);
            println("    - Guardian Pseudonym: %s", guardianPseudonym);
        }
    }

    ////////
    // one person, one account
    ////////

    /** Demonstrates a (rejected) duplicate verification. */
    private static void duplicateVerification() throws IOException {
        duplicateVerification(SITE1_NAME, SITE1_PORT, "drop-table", "Bobby Tables", "John Smith", true);
        displayBorder();
    }

    /** Tries to verify a second account using the same person. */
    private static void duplicateVerification(
            String siteName, int sitePort, String siteAccountId, String person, String otherPerson, boolean verbose)
            throws IOException {
        println("%s uses %s to verify his account on %s, \"%s\":", person, AVS_NAME, siteName, siteAccountId);
        println("- On %s, %s begins the process to verify \"%s\".", siteName, person, siteAccountId);
        String requestUrl = verificationRequestUrl(sitePort);
        VerificationRequest request = createVerificationRequest(requestUrl, siteAccountId);
        if (verbose) {
            displayExchange("POST", requestUrl, request);
        }

        println("- %s redirects %s to CheckMyAge.", siteName, person);
        linkVerificationRequest(request.redirectUrl(), otherPerson);
        if (verbose) {
            displayExchange("POST", request.redirectUrl(), 200);
        }

        println("- %s tries to use %s's account on %s to verify his account.", person, otherPerson, AVS_NAME);
        println("- (Behind the scenes, %s sends an age certificate to %s.)", AVS_NAME, siteName);
        String certificateUrl = ageCertificateUrl();
        HttpOptional<String> maybeRedirectUrl = sendAgeCertificate(certificateUrl, otherPerson);
        if (maybeRedirectUrl.statusCode() != 409) {
            error();
        }
        if (verbose) {
            displayExchange("POST", certificateUrl, maybeRedirectUrl.statusCode());
        }

        println("- %s rejects the age certificate!", siteName);
        println();
    }

    ////////
    // data breach!
    ////////

    /** Displays the user data stored on all sites. */
    private static void displayUserData() throws IOException {
        displayUserData(SITE1_NAME, SITE1_PORT, "publius", "publius-jr");
        displayUserData(SITE2_NAME, SITE2_PORT, "JohnS", "BillyS");
        displayUserData(AVS_NAME, AVS_PORT, "John Smith", "Billy Smith");
        displayBorder();
    }

    /** Displays the user data stored on a site for the specified accounts. */
    private static void displayUserData(String name, int port, String... accountIds) throws IOException {
        Map<String, VerifiedUser> userData = new LinkedHashMap<>();
        for (String accountId : accountIds) {
            String url = verificationStateUrl(port);
            VerificationState state = getVerificationState(url, accountId);
            if (state.status() != VerificationStatus.VERIFIED) {
                error();
            }

            VerifiedUser user = state.verifiedUser();
            userData.put(accountId, user);
        }
        println("User data stored on %s:\n%s\n", name, prettyJson(userData));
    }

    ////////
    // JSON API
    ////////

    /** Builds the URL to create a verification request. */
    private static String verificationRequestUrl(int port) {
        return url(port, "/api/verification-request/create");
    }

    /** Creates a verification request for an account. */
    private static VerificationRequest createVerificationRequest(String url, String siteAccountId) throws IOException {
        HttpOptional<VerificationRequest> maybeRequest = JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<VerificationRequest>() {})
                .post(url)
                .header("Account-Id", siteAccountId)
                .build()
                .execute();
        if (maybeRequest.isEmpty()) {
            error();
        }
        return maybeRequest.get();
    }

    /** Links a verification request to a person. */
    private static void linkVerificationRequest(String url, String avsAccountId) throws IOException {
        int statusCode = JsonApiClient.requestBuilder()
                .statusCodeResponse()
                .post(url)
                .header("Account-Id", avsAccountId)
                .build()
                .execute();
        if (statusCode != 200) {
            error();
        }
    }

    /** Builds the URL to send an age certificate. */
    private static String ageCertificateUrl() {
        return url(AVS_PORT, "/api/age-certificate/send");
    }

    /** Sends an age certificate for a person to a site. */
    private static HttpOptional<String> sendAgeCertificate(String url, String avsAccountId) throws IOException {
        return JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<String>() {})
                .post(url)
                .header("Account-Id", avsAccountId)
                .build()
                .execute();
    }

    /** Builds the URL to get the verification state. */
    private static String verificationStateUrl(int port) {
        return url(port, "/api/verification-state");
    }

    /** Gets the verification state for an account. */
    private static VerificationState getVerificationState(String url, String siteAccountId) throws IOException {
        HttpOptional<VerificationState> maybeState = JsonApiClient.requestBuilder()
                .jsonResponse(new TypeReference<VerificationState>() {})
                .get(url)
                .header("Account-Id", siteAccountId)
                .build()
                .execute();
        if (maybeState.isEmpty()) {
            error();
        }
        return maybeState.get();
    }

    ////////
    // utilities
    ////////

    /** Builds a URL for localhost at the specified port and path. */
    private static String url(int port, String path) {
        path = path.replaceFirst("^/", "");
        return String.format("http://localhost:%d/%s", port, path);
    }

    /** Converts an object to pretty-printed JSON. */
    private static String prettyJson(Object value) {
        try {
            return jsonWriter.writeValueAsString(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Displays an HTTP request and the corresponding HTTP response. */
    private static void displayExchange(String requestMethod, String requestUrl, int responseStatusCode) {
        println("[HTTP request]");
        println("%s %s", requestMethod, requestUrl);
        println("[HTTP response]");
        println("%d", responseStatusCode);
        println();
    }

    /** Displays an HTTP request and the corresponding HTTP response. */
    private static void displayExchange(String requestMethod, String requestUrl, Object responseValue) {
        println("[HTTP request]");
        println("%s %s", requestMethod, requestUrl);
        println("[HTTP response]");
        println("200");
        println(prettyJson(responseValue));
        println();
    }

    /** Starts all the servers. */
    private static void startServers() {
        siteServer1.start();
        siteServer2.start();
        avsServer.start();
    }

    /** Stops all the servers. */
    private static void stopServers() {
        siteServer1.stop();
        siteServer2.stop();
        avsServer.stop();
    }

    /** Called when an error occurred. */
    private static void error() {
        println("***Error occurred!***");
        throw new AssertionError();
    }

    /** Displays a border. */
    private static void displayBorder() {
        println("================================================================================");
        println();
    }

    /** Syntactic sugar for printing a line. */
    private static void println(String text) {
        System.out.println(text);
    }

    /** Syntactic sugar for printing a formatted line. */
    @FormatMethod
    private static void println(String format, Object... args) {
        System.out.format(format, args).println();
    }

    /** Syntactic sugar for printing an empty line. */
    private static void println() {
        System.out.println();
    }

    // static class
    private Main() {}
}
