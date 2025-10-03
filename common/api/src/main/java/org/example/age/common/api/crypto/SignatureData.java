package org.example.age.common.api.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.nio.charset.StandardCharsets;
import java.security.Signature;

/** Signature where the message format is compact JSON. Encoded in a URL-friendly base64 format. */
public final class SignatureData extends ImmutableBytes {

    /** Creates a signature for an object that is serialized as compact JSON. */
    public static SignatureData sign(Signature signer, String json) {
        try {
            signer.update(json.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = signer.sign();
            return new SignatureData(bytes);
        } catch (Exception e) {
            throw new RuntimeException("error creating signature", e);
        }
    }

    /** Converts a string to a signature for the purpose of JSON deserialization. */
    @JsonCreator
    public static SignatureData fromString(String text) {
        byte[] bytes = bytesFromString(text);
        return new SignatureData(bytes);
    }

    /** Verifies the signature against an object that is serialized as compact JSON. */
    public void verify(Signature verifier, String json) {
        try {
            verifier.update(json.getBytes(StandardCharsets.UTF_8));
            boolean verified = verifier.verify(bytes);
            if (!verified) {
                throw new IllegalArgumentException("signature verification failed");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("signature verification failed", e);
        }
    }

    private SignatureData(byte[] bytes) {
        super(bytes);
    }
}
