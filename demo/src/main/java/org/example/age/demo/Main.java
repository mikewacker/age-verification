package org.example.age.demo;

import com.google.errorprone.annotations.FormatMethod;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import org.example.age.data.AgeRange;
import org.example.age.data.AgeThresholds;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.verification.AvsUi;
import org.example.age.verification.SiteUi;
import org.example.age.verification.VerifiedUserStore;

/** Demos a proof-of-concept for age verification. */
public final class Main {

    private static final Demo demo = Demo.create();

    /** Main method. */
    public static void main(String[] args) {
        displayBorder();

        // high-level workflow for all sites
        verifyParentAndChildForAvs();
        verifyParentAndChildForSite(Demo.SITE1_NAME, Demo.PARENT_SITE1_USERNAME, Demo.CHILD_SITE1_USERNAME);
        verifyParentAndChildForSite(Demo.SITE2_NAME, Demo.PARENT_SITE2_USERNAME, Demo.CHILD_SITE2_USERNAME);
        displayBorder();

        // display the user data stored
        displayParentAndChildDataForSite(Demo.AVS_NAME, Demo.PARENT_REAL_NAME, Demo.CHILD_REAL_NAME);
        displayParentAndChildDataForSite(Demo.SITE1_NAME, Demo.PARENT_SITE1_USERNAME, Demo.CHILD_SITE1_USERNAME);
        displayParentAndChildDataForSite(Demo.SITE2_NAME, Demo.PARENT_SITE2_USERNAME, Demo.CHILD_SITE2_USERNAME);
        displayBorder();

        // detailed workflow for a single account
        verifyUserForSiteVerbose(Demo.SITE2_NAME, Demo.CHILD_REAL_NAME, Demo.CHILD_SITE2_USERNAME);
        displayBorder();
    }

    /** Verifies the age and guardian relationship of a parent and child for the age verification service. */
    private static void verifyParentAndChildForAvs() {
        displayIntro();
        verifyPersonForAvs(Demo.PARENT_REAL_NAME, Demo.PARENT_AGE, Optional.empty());
        verifyPersonForAvs(Demo.CHILD_REAL_NAME, Demo.CHILD_AGE, Optional.of(Demo.PARENT_REAL_NAME));
        println();
    }

    /** Displays some introductory text. */
    private static void displayIntro() {
        String avsName = demo.avsUi().getName();
        String site1Name = demo.siteUi(Demo.SITE1_NAME).getName();
        String site2Name = demo.siteUi(Demo.SITE2_NAME).getName();

        println("In this proof-of-concept:");
        println("- %s is a third-party age verification service.", avsName);
        println("- %s and %s are social media sites that have registered with %s.", site1Name, site2Name, avsName);
        println(
                "- %s and %s need to know whether a user's age is %s.",
                site1Name, site2Name, ageThresholds(Demo.AGE_THRESHOLDS));
        println();
    }

    /** Verifies the age and guardian (if applicable) of a person for the age verification service. */
    private static void verifyPersonForAvs(String realName, int age, Optional<String> maybeGuardian) {
        println("%s has already verified his identity on %s:", realName, Demo.AVS_NAME);
        println("- Age: %d", age);
        if (maybeGuardian.isPresent()) {
            String guardian = maybeGuardian.get();
            println("- Guardian: %s", guardian);
        }
    }

    /** Verifies the age and guardian relationship of a parent and child for a site. */
    private static void verifyParentAndChildForSite(String siteId, String parentUsername, String childUsername) {
        AvsUi avsUi = demo.avsUi();
        SiteUi siteUi = demo.siteUi(siteId);

        verifyUserForSite(avsUi, siteUi, Demo.PARENT_REAL_NAME, parentUsername);
        verifyUserForSite(avsUi, siteUi, Demo.CHILD_REAL_NAME, childUsername);
        println();
    }

    /** Verifies the age and guardian (if applicable) of a user for a site. */
    private static void verifyUserForSite(AvsUi avsUi, SiteUi siteUi, String realName, String username) {
        // Run the workflow.
        VerificationRequest request = siteUi.createVerificationRequest(username);
        avsUi.processVerificationRequest(realName, request.id());

        // Display the results.
        println("%s uses %s to verify \"%s\" on %s:", realName, avsUi.getName(), username, siteUi.getName());
        println("- Age: %s", siteUi.getAgeRange(username));
        if (!siteUi.getGuardians(username).isEmpty()) {
            String guardian = siteUi.getGuardians(username).get(0);
            println("- Guardian: %s", guardian);
        }
    }

    /** Displays the data for a parent and child that is stored on a site or service. */
    private static void displayParentAndChildDataForSite(String name, String parentUsername, String childUsername) {
        VerifiedUserStore userStore = demo.verifiedUserStore(name);

        println("Data stored on %s:", name);
        displayUserDataForSite(userStore, parentUsername);
        displayUserDataForSite(userStore, childUsername);
        println();
    }

    /** Displays the data for a user that is stored on the site or service. */
    private static void displayUserDataForSite(VerifiedUserStore userStore, String username) {
        VerifiedUser user = userStore.retrieveVerifiedUser(username);

        println("- %s:", username);
        displayVerifiedUser(user);
    }

    /** Verifies the age and guardian (if applicable) of a user for a site. Displays verbose output. */
    private static void verifyUserForSiteVerbose(String siteId, String realName, String username) {
        AvsUi avsUi = demo.avsUi();
        SiteUi siteUi = demo.siteUi(siteId);
        VerifiedUserStore avsUserStore = demo.verifiedUserStore(Demo.AVS_NAME);
        VerifiedUserStore siteUserStore = demo.verifiedUserStore(siteId);

        // Run the workflow.
        VerificationRequest request = siteUi.createVerificationRequest(username);
        avsUi.processVerificationRequest(realName, request.id());

        // Get verbose information.
        VerifiedUser avsUser = avsUserStore.retrieveVerifiedUser(realName);
        AgeCertificate certificate = demo.ageCertificate(request.id());
        VerifiedUser siteUser = siteUserStore.retrieveVerifiedUser(username);

        // Display the detailed workflow.
        String avsName = avsUi.getName();
        String siteName = siteUi.getName();

        println("Detailed workflow to verify \"%s\" on %s:", username, siteName);
        println("- The current time is %s.", time(System.currentTimeMillis() / 1000));
        println("- [#1], [#2] is used to denote when data is stored that will be used later.");
        println();
        println("For a proof-of-concept, everything runs on a single machine;");
        println("the demo pretends that %s and %s are separate websites.", avsName, siteName);
        println("(For the real thing, we assume that engineers know how to build a website.)");
        println();
        println("Part I");
        println("- %s logs in as \"%s\" on %s.", realName, username, siteName);
        println("- %s starts the process to verify \"%s\".", realName, username);
        println("- %s asks %s to create a new verification request.", siteName, avsName);
        println("- %s generates the following verification request:", avsName);
        displayVerificationRequest(request);
        println("- [#1] %s stores a copy of the verification request.", avsName);
        println("- %s sends the verification request back to %s.", avsName, siteName);
        println("- [#2] %s links the request ID (%s) to \"%s\".", siteName, shortId(request.id()), username);
        println("- %s opens the following URL in a new window on %s's browser:", siteName, realName);
        println("  %s", avsUrl(avsName, request.id()));
        println();

        println("Part II");
        println("- %s receives a web request at the following URL:", avsName);
        println("  %s", avsUrl(avsName, request.id()));
        println("- %s gets the verification request ID (%s) from the URL.", avsName, shortId(request.id()));
        println("- [#1] %s retrieves the full verification request:", avsName);
        displayVerificationRequest(request);
        println("- %s checks that the verification request is not expired.", avsName);
        println("- %s responds to the web request by displaying a login page.", avsName);
        println("- %s logs in to %s.", realName, avsName);
        println("- %s links the request ID (%s) to %s.", avsName, shortId(request.id()), realName);
        println("- %s confirms that %s wants to verify an account on %s.", avsName, realName, siteName);
        println("- %s retrieves the user data for %s:", avsName, realName);
        displayVerifiedUser(avsUser);
        println(
                "- %s anonymizes the age that it will share with %s: %s",
                avsName, siteName, certificate.verifiedUser().ageRange());
        println("- %s changes the pseudonyms using its secret key for %s.", avsName, siteName);
        println("- Updated user data:");
        displayVerifiedUser(certificate.verifiedUser());
        println("- %s creates the age certificate:", avsName);
        displayAgeCertificate(certificate);
        println("- %s digitally signs the age certificate.", avsName);
        println("- %s securely transmits the signed age certificate to %s.", avsName, siteName);
        println();

        println("Part III");
        println("- %s receives a signed age certificate:", siteName);
        displayAgeCertificate(certificate);
        println("- %s verifies the signed age certificate.", siteName);
        println("    - It verifies that the age certificate is signed by %s.", avsName);
        println("    - It verifies that %s is the recipient in the \"Site\" field.", siteName);
        println("    - It verifies that the age certificate is not expired.");
        println("- [#2] %s matches the request ID (%s) to \"%s\".", siteName, shortId(request.id()), username);
        println("- %s extracts the user data from the age certificate:", siteName);
        displayVerifiedUser(certificate.verifiedUser());
        println("- %s changes the pseudonyms using a secret key.", siteName);
        println("- Updated user data:");
        displayVerifiedUser(siteUser);
        println(
                "- %s checks that no other accounts have the same pseudonym (%s).",
                siteName, shortId(siteUser.pseudonym()));
        println("- %s stores this user data for \"%s\". \"%s\" is now verified!", siteName, username, username);
        println();
    }

    /** Displays an age certificate. */
    private static void displayAgeCertificate(AgeCertificate certificate) {
        displayVerificationRequest(certificate.verificationRequest());
        displayVerifiedUser(certificate.verifiedUser());
    }

    /** Displays a verification request. */
    private static void displayVerificationRequest(VerificationRequest request) {
        println("    - Request ID: %s", request.id());
        println("    - Site: %s", request.siteId());
        println("    - Expiration: %s", time(request.expiration()));
    }

    /** Displays a verified user. */
    private static void displayVerifiedUser(VerifiedUser user) {
        println("    - Pseudonym: %s", user.pseudonym());
        println("    - Age: %s", user.ageRange());
        if (!user.guardianPseudonyms().isEmpty()) {
            SecureId guardianPseudonym = user.guardianPseudonyms().get(0);
            println("    - Guardian Pseudonym: %s", guardianPseudonym);
        }
    }

    /** Formats age thresholds. */
    private static String ageThresholds(AgeThresholds ageThresholds) {
        List<AgeRange> ageRanges = ageThresholds.getAgeRanges();
        int lastIndex = ageRanges.size() - 1;
        String last = ageRanges.get(lastIndex).toString();
        String others =
                ageRanges.subList(0, lastIndex).stream().map(AgeRange::toString).collect(Collectors.joining(", "));
        return String.format("%s, %s", others, last);
    }

    /** Formats the URL to verify an account on the age verification service. */
    private static String avsUrl(String avsName, SecureId requestId) {
        return String.format("https://www.%s.com/verify/%s", avsName.toLowerCase(Locale.US), requestId);
    }

    /** Formats an ID in a shortened form. */
    private static String shortId(SecureId id) {
        String idText = id.toString();
        return String.format("%s...", idText.substring(0, 8));
    }

    /** Formats an epoch time. */
    private static String time(long epoch) {
        ZonedDateTime localTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.systemDefault());
        return localTime.format(DateTimeFormatter.ofPattern("LLLL d, yyyy, h:mm:ss a z"));
    }

    /** Displays a border. */
    private static void displayBorder() {
        println("================================================================================");
        println();
    }

    /** Syntactic sugar for printing a line that may be formatted. */
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
