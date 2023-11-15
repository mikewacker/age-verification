package org.example.age.verification;

import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.example.age.data.AgeThresholds;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.BytesValue;
import org.example.age.data.crypto.SecureId;

/**
 * Proof-of-concept implementation of an age verification component for the age verification service.
 *
 * <p>For a proof-of-concept, we won't be building websites, establishing TLS sessions, etc.
 * The comments for the interfaces indicate how these methods would correspond to the real workflow.</p>
 */
public final class AvsVerificationComponent implements AvsUi, AvsApi, VerifiedUserStore {

    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);

    private final String name;
    private final KeyPair signingKeyPair;

    // We wouldn't use a real name as a map key IRL, but it's fine for a proof-of-concept.
    private final Map<String, VerifiedUser> users = new HashMap<>();
    private final Map<String, Site> sites = new HashMap<>();
    private final Map<SecureId, VerificationRequest> pendingRequests = new HashMap<>();

    /** Creates the age verification component for the age verification service. */
    public static AvsVerificationComponent create(String name, KeyPair signingKeyPair) {
        return new AvsVerificationComponent(name, signingKeyPair);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PublicKey getPublicSigningKey() {
        return signingKeyPair.getPublic();
    }

    @Override
    public VerifiedUser retrieveVerifiedUser(String realName) {
        Optional<VerifiedUser> maybeUser = Optional.ofNullable(users.get(realName));
        if (maybeUser.isEmpty()) {
            throw new IllegalArgumentException("person not found");
        }

        return maybeUser.get();
    }

    /**
     * Registers a verified person.
     *
     * <p>In the real workflow, the age verification service would construct the {@link VerifiedUser} object
     * after it has verified the person's age and guardians (if applicable).</p>
     */
    public void registerPerson(String realName, VerifiedUser user) {
        users.put(realName, user);
    }

    /**
     * Registers a site.
     *
     * <p>The remote pseudonym key is generated by the age verification service; it is not shared with the site.</p>
     */
    public void registerSite(
            String siteId, SiteApi certificateProcessor, AgeThresholds ageThresholds, SecureId remotePseudonymKey) {
        Site siteInfo = new Site(siteId, certificateProcessor, ageThresholds, remotePseudonymKey);
        sites.put(siteId, siteInfo);
    }

    @Override
    public VerificationRequest generateVerificationRequestForSite(String siteId) {
        if (!sites.containsKey(siteId)) {
            throw new IllegalArgumentException("site not registered");
        }

        VerificationRequest request = VerificationRequest.generateForSite(siteId, EXPIRES_IN);
        pendingRequests.put(request.id(), request);
        return request;
    }

    @Override
    public void processVerificationRequest(String realName, SecureId requestId) {
        VerifiedUser user = retrieveVerifiedUser(realName);
        VerificationRequest request = retrievePendingVerificationRequest(requestId);
        Site site = sites.get(request.siteId());
        AgeCertificate certificate = createAgeCertificateForSite(user, request, site);
        transmitAgeCertificateToSite(certificate, site);
    }

    /** Retrieves a non-expired verification request. */
    private VerificationRequest retrievePendingVerificationRequest(SecureId requestId) {
        Optional<VerificationRequest> maybeRequest = Optional.ofNullable(pendingRequests.remove(requestId));
        if (maybeRequest.isEmpty()) {
            throw new IllegalArgumentException("verification request not found");
        }
        VerificationRequest request = maybeRequest.get();

        if (request.isExpired()) {
            throw new IllegalStateException("verification request expired");
        }

        return request;
    }

    /** Creates an age certificate to verify an account on the site. */
    private AgeCertificate createAgeCertificateForSite(VerifiedUser user, VerificationRequest request, Site site) {
        VerifiedUser localUser = user.anonymizeAge(site.ageThresholds()).localize(site.remotePseudonymKey());
        AesGcmEncryptionPackage authToken =
                AesGcmEncryptionPackage.of(BytesValue.ofBytes(new byte[1]), BytesValue.ofBytes(new byte[1]));
        return AgeCertificate.of(request, localUser, authToken);
    }

    /** Transmits an age certificate to the site. */
    private void transmitAgeCertificateToSite(AgeCertificate certificate, Site site) {
        byte[] signedCertificate = certificate.sign(signingKeyPair.getPrivate());
        site.api().processAgeCertificate(signedCertificate);
    }

    private AvsVerificationComponent(String name, KeyPair signingKeyPair) {
        this.name = name;
        this.signingKeyPair = signingKeyPair;
    }

    /** Information stored for a registered site. */
    @SuppressWarnings("UnusedVariable") // false positive, see https://github.com/google/error-prone/issues/2713
    private record Site(String id, SiteApi api, AgeThresholds ageThresholds, SecureId remotePseudonymKey) {}
}
