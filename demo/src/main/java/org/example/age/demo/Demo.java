package org.example.age.demo;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyPair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.age.data.AgeThresholds;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.verification.AvsApi;
import org.example.age.verification.AvsUi;
import org.example.age.verification.AvsVerificationComponent;
import org.example.age.verification.SiteApi;
import org.example.age.verification.SiteUi;
import org.example.age.verification.SiteVerificationComponent;
import org.example.age.verification.VerifiedUserStore;

/** Demo consisting of an age verification service, two social media sites, and a parent and child. */
public final class Demo {

    public static final String AVS_NAME = "CheckMyAge";
    public static final String SITE1_NAME = "Crackle";
    public static final String SITE2_NAME = "Pop";

    public static final String PARENT_REAL_NAME = "John Smith";
    public static final int PARENT_AGE = 40;
    public static final String PARENT_SITE1_USERNAME = "JohnS";
    public static final String PARENT_SITE2_USERNAME = "publius";

    public static final String CHILD_REAL_NAME = "Bobby Smith";
    public static final int CHILD_AGE = 13;
    public static final String CHILD_SITE1_USERNAME = "BobbyS";
    public static final String CHILD_SITE2_USERNAME = "publius-jr";

    public static final AgeThresholds AGE_THRESHOLDS = AgeThresholds.of(13, 18);

    private AvsUi avsUi;
    private final Map<String, SiteUi> siteUis = new HashMap<>();
    private final Map<String, VerifiedUserStore> userStores = new HashMap<>();
    private final Map<SecureId, AgeCertificate> certificateStore = new HashMap<>();

    /** Creates the demo. */
    public static Demo create() {
        return new Demo();
    }

    /** UI for the age verification service. */
    public AvsUi avsUi() {
        return avsUi;
    }

    /** UI for a social media site. */
    public SiteUi siteUi(String siteId) {
        return siteUis.get(siteId);
    }

    /** Verified user store for a site or service. */
    public VerifiedUserStore verifiedUserStore(String name) {
        return userStores.get(name);
    }

    /** Age certificate that was created for a verification request. */
    public AgeCertificate ageCertificate(SecureId requestId) {
        return certificateStore.get(requestId);
    }

    /** Sets up the age verification components. */
    private Demo() {
        try {
            // Set up the age verification service.
            AvsVerificationComponent avs = createAvs();
            registerPeople(avs);

            // Set up the sites.
            SiteVerificationComponent site1 = createSite(SITE1_NAME, avs);
            registerSite(avs, SITE1_NAME, site1, AGE_THRESHOLDS);

            SiteVerificationComponent site2 = createSite(SITE2_NAME, avs);
            registerSite(avs, SITE2_NAME, site2, AGE_THRESHOLDS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates the age verification component for the age verification service. */
    private AvsVerificationComponent createAvs() throws IOException {
        Path keysDir = Path.of("keys", AVS_NAME);

        KeyPair keyPair = Resources.loadEd25519KeyPair(keysDir.resolve("signing.pem"));
        AvsVerificationComponent avs = AvsVerificationComponent.create(AVS_NAME, keyPair);
        avsUi = avs;
        userStores.put(AVS_NAME, avs);
        return avs;
    }

    /** Registers people with the age verification service. */
    private void registerPeople(AvsVerificationComponent avs) throws IOException {
        Path dataDir = Path.of("data", AVS_NAME);

        SecureId parentPseudonym = Resources.loadSecureId(dataDir.resolve("parent-pseudonym.bin"));
        VerifiedUser parent = VerifiedUser.of(parentPseudonym, PARENT_AGE);
        avs.registerPerson(PARENT_REAL_NAME, parent);

        SecureId childPseudonym = Resources.loadSecureId(dataDir.resolve("child-pseudonym.bin"));
        VerifiedUser child = VerifiedUser.of(childPseudonym, CHILD_AGE, List.of(parentPseudonym));
        avs.registerPerson(CHILD_REAL_NAME, child);
    }

    /** Creates the age verification component for a site. */
    private SiteVerificationComponent createSite(String siteId, AvsApi avsApi) throws IOException {
        Path keysDir = Path.of("keys", siteId);

        SecureId localPseudonymKey = Resources.loadSecureId(keysDir.resolve("pseudonym.bin"));
        SiteVerificationComponent site = SiteVerificationComponent.create(siteId, localPseudonymKey, avsApi);
        siteUis.put(siteId, site);
        userStores.put(siteId, site);
        return site;
    }

    /** Registers a site with the age verification service. */
    private void registerSite(AvsVerificationComponent avs, String siteId, SiteApi siteApi, AgeThresholds ageThresholds)
            throws IOException {
        Path keysDir = Path.of("keys", AVS_NAME, "sites", siteId);

        SiteApi mitm = new CertificateStoreMitm(siteApi, siteId, avs, certificateStore);
        SecureId remotePseudonymKey = Resources.loadSecureId(keysDir.resolve("pseudonym.bin"));
        avs.registerSite(siteId, mitm, ageThresholds, remotePseudonymKey);
    }

    /**
     * Man-in-the-middle that stores age certificates.
     *
     * <p>Used strictly for demo purposes.</p>
     */
    @SuppressWarnings("UnusedVariable") // false positive, see https://github.com/google/error-prone/issues/2713
    private record CertificateStoreMitm(
            SiteApi delegate, String siteId, AvsApi avsApi, Map<SecureId, AgeCertificate> certificateStore)
            implements SiteApi {

        @Override
        public void processAgeCertificate(byte[] signedCertificate) {
            delegate.processAgeCertificate(signedCertificate);
            AgeCertificate certificate =
                    AgeCertificate.verifyForSite(signedCertificate, avsApi.getPublicSigningKey(), siteId);
            certificateStore.put(certificate.verificationRequest().id(), certificate);
        }
    }
}
