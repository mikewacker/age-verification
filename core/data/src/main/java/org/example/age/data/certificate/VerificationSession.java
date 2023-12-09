package org.example.age.data.certificate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.json.JsonStyle;
import org.immutables.value.Value;

/** Session to pseudonymously verify a person's age (and guardians, if applicable). */
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

    /** Creates a verification session for the verification request. */
    static VerificationSession create(VerificationRequest request) {
        Aes256Key authKey = Aes256Key.generate();
        return of(request, authKey);
    }

    /** Verification request. */
    VerificationRequest verificationRequest();

    /** Ephemeral key used to encrypt and decrypt authentication data. */
    Aes256Key authKey();
}
