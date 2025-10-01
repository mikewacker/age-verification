package org.example.age.testing.api;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.example.age.common.api.AgeCertificate;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;

/** Creates model objects for testing. */
public final class TestModels {

    /** Creates an age certificate. */
    public static AgeCertificate createAgeCertificate() {
        VerificationRequest request = createVerificationRequest("site");
        return createAgeCertificate(request);
    }

    /** Creates an age certificate for the verification request. */
    public static AgeCertificate createAgeCertificate(VerificationRequest request) {
        VerifiedUser user = createVerifiedUser();
        return AgeCertificate.builder().request(request).user(user).build();
    }

    /** Creates a verification request for the site. */
    public static VerificationRequest createVerificationRequest(String siteId) {
        OffsetDateTime expiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(5));
        return VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId(siteId)
                .expiration(expiration)
                .build();
    }

    /** Creates a verified user. */
    public static VerifiedUser createVerifiedUser() {
        return VerifiedUser.builder()
                .pseudonym(SecureId.generate())
                .ageRange(AgeRange.builder().min(18).max(18).build())
                .build();
    }

    private TestModels() {} // static class
}
