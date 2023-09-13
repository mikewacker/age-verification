package org.example.age.verification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.example.age.certificate.AgeCertificate;
import org.example.age.certificate.VerificationRequest;
import org.example.age.data.AgeRange;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;

/**
 * Proof-of-concept implementation of an age verification component for a social media site.
 *
 * <p>For a proof-of-concept, we won't be building websites, establishing TLS sessions, etc.
 * The comments for the interfaces indicate how these methods would correspond to the real workflow.</p>
 */
public final class SiteVerificationComponent implements SiteUi, SiteApi, VerifiedUserStore {

    private final String siteId;
    private final SecureId localPseudonymKey;
    private final AvsApi avsApi;

    private final Map<String, VerifiedUser> users = new HashMap<>();
    private final Map<SecureId, String> pendingUsernames = new HashMap<>();
    private final Map<SecureId, String> verifiedUsernames = new HashMap<>();

    /** Creates the age verification component for a site. */
    public static SiteVerificationComponent create(String siteId, SecureId localPseudonymKey, AvsApi avsApi) {
        return new SiteVerificationComponent(siteId, localPseudonymKey, avsApi);
    }

    @Override
    public String getName() {
        return siteId;
    }

    @Override
    public boolean isVerified(String username) {
        return users.containsKey(username);
    }

    @Override
    public AgeRange getAgeRange(String username) {
        VerifiedUser user = retrieveVerifiedUser(username);
        return user.ageRange();
    }

    @Override
    public List<String> getGuardians(String username) {
        VerifiedUser user = retrieveVerifiedUser(username);
        return user.guardianPseudonyms().stream()
                .map(verifiedUsernames::get)
                .map(Optional::ofNullable)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public VerifiedUser retrieveVerifiedUser(String username) {
        Optional<VerifiedUser> maybeUser = Optional.ofNullable(users.get(username));
        if (maybeUser.isEmpty()) {
            throw new IllegalArgumentException("user not verified");
        }

        return maybeUser.get();
    }

    @Override
    public VerificationRequest createVerificationRequest(String username) {
        VerificationRequest request = avsApi.generateVerificationRequestForSite(siteId);
        pendingUsernames.put(request.id(), username);
        return request;
    }

    @Override
    public void processAgeCertificate(byte[] signedCertificate) {
        AgeCertificate certificate =
                AgeCertificate.verifyForSite(signedCertificate, avsApi.getPublicSigningKey(), siteId);
        String username = matchVerificationRequestToUsername(certificate.verificationRequest());
        VerifiedUser localUser = localizeVerifiedUser(certificate.verifiedUser());
        checkNoDuplicatePseudonyms(username, localUser);
        storeVerifiedUser(username, localUser);
    }

    /** Matches the verification request to a username. */
    private String matchVerificationRequestToUsername(VerificationRequest request) {
        Optional<String> maybeUsername = Optional.ofNullable(pendingUsernames.remove(request.id()));
        if (maybeUsername.isEmpty()) {
            throw new IllegalArgumentException("verification request is not linked to a username");
        }

        return maybeUsername.get();
    }

    /** Localizes a verified user for this site. */
    private VerifiedUser localizeVerifiedUser(VerifiedUser user) {
        return user.localize(localPseudonymKey);
    }

    /** Checks that the same pseudonym is not used to verify multiple accounts. */
    private void checkNoDuplicatePseudonyms(String username, VerifiedUser localUser) {
        Optional<String> maybeVerifiedUsername = Optional.ofNullable(verifiedUsernames.get(localUser.pseudonym()));
        if (maybeVerifiedUsername.isEmpty()) {
            return;
        }
        String verifiedUsername = maybeVerifiedUsername.get();

        if (!username.equals(verifiedUsername)) {
            throw new IllegalStateException("one person cannot verify multiple accounts on a single site");
        }
    }

    /** Stores a verified user. */
    private void storeVerifiedUser(String username, VerifiedUser localUser) {
        users.put(username, localUser);
        verifiedUsernames.put(localUser.pseudonym(), username);
    }

    private SiteVerificationComponent(String siteId, SecureId localPseudonymKey, AvsApi avsApi) {
        this.siteId = siteId;
        this.localPseudonymKey = localPseudonymKey;
        this.avsApi = avsApi;
    }
}
