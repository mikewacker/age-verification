package org.example.age.data.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.age.data.DataStyle;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.internal.SerializationUtils;
import org.immutables.value.Value;

/** Session to pseudonymously verify a person's age (and guardians, if applicable). */
@Value.Immutable
@DataStyle
@JsonSerialize(as = ImmutableVerificationSession.class)
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

    /** Deserializes the session from raw bytes. */
    static VerificationSession deserialize(byte[] bytes) {
        return SerializationUtils.deserialize(bytes, VerificationSession.class);
    }

    /** Verification request. */
    VerificationRequest verificationRequest();

    /** Ephemeral key used to encrypt and decrypt authentication data. */
    Aes256Key authKey();

    /** Serializes the session to raw bytes. */
    @Value.Lazy
    @JsonIgnore
    default byte[] serialize() {
        return SerializationUtils.serialize(this);
    }
}
