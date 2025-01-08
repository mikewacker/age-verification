package org.example.age.module.crypto.demo.testing;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.example.age.api.AgeCertificate;
import org.example.age.api.AgeRange;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.SecureId;

/** {@link AgeCertificate} singleton for testing. */
public final class TestAgeCertificate {

    private static final AgeCertificate ageCertificate = create();

    /** Gets the {@link AgeCertificate}. */
    public static AgeCertificate get() {
        return ageCertificate;
    }

    /** Creates the {@link AgeCertificate}. */
    private static AgeCertificate create() {
        VerificationRequest request = VerificationRequest.builder()
                .id(SecureId.generate())
                .siteId("site")
                .expiration(OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(5)))
                .build();
        VerifiedUser user = VerifiedUser.builder()
                .pseudonym(SecureId.generate())
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        return AgeCertificate.builder().request(request).user(user).build();
    }

    private TestAgeCertificate() {}
}
