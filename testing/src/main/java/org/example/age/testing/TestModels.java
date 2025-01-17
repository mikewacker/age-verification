package org.example.age.testing;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.example.age.api.AgeCertificate;
import org.example.age.api.AgeRange;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.SecureId;

/** Creates model objects for testing. */
public final class TestModels {

    /** Creates an {@link AgeCertificate}. */
    public static AgeCertificate createAgeCertificate() {
        VerificationRequest request = createVerificationRequest("site");
        return createAgeCertificate(request);
    }

    /** Creates an {@link AgeCertificate} for the {@link VerificationRequest}. */
    public static AgeCertificate createAgeCertificate(VerificationRequest request) {
        VerifiedUser user = createVerifiedUser();
        return AgeCertificate.builder().request(request).user(user).build();
    }

    /** Creates a {@link VerificationRequest} for the site. */
    public static VerificationRequest createVerificationRequest(String siteId) {
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(5));
        return VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId(siteId)
                .expiration(expiration)
                .build();
    }

    /** Creates a {@link VerifiedUser}. */
    public static VerifiedUser createVerifiedUser() {
        return VerifiedUser.builder()
                .pseudonym(SecureId.generate())
                .ageRange(AgeRange.builder().min(18).max(18).build())
                .build();
    }

    // static class
    private TestModels() {}
}
