package org.example.age.data.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.mikewacker.drift.json.JsonStyle;
import org.example.age.data.crypto.Aes256Key;
import org.immutables.value.Value;

/** Session to verify an account on a site. */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableVerificationSession.class)
public interface VerificationSession {

    /** Creates a verification session. */
    static VerificationSession of(VerificationRequest request, Aes256Key authKey) {
        return ImmutableVerificationSession.builder()
                .verificationRequest(request)
                .authKey(authKey)
                .build();
    }

    /** Generates a verification session for the verification request. */
    static VerificationSession generate(VerificationRequest request) {
        Aes256Key authKey = Aes256Key.generate();
        return of(request, authKey);
    }

    /** Verification request. */
    VerificationRequest verificationRequest();

    /** Ephemeral key used to encrypt and decrypt authentication data. */
    Aes256Key authKey();
}
