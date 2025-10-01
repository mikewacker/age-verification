package org.example.age.common.api.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.security.Signature;

/** Signature where the message format is compact JSON. Encoded in a URL-friendly base64 format. */
public final class SignatureData extends ImmutableBytes {

    /** Creates a signature for an object that is serialized as compact JSON. */
    public static SignatureData sign(Object o, ObjectMapper mapper, Signature signer) {
        byte[] message = getMessage(o, mapper);
        try {
            signer.update(message);
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
    public void verify(Object o, ObjectMapper mapper, Signature verifier) {
        byte[] message = getMessage(o, mapper);
        try {
            verifier.update(message);
            boolean verified = verifier.verify(bytes);
            if (!verified) {
                throw new IllegalArgumentException("signature verification failed");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("signature verification failed", e);
        }
    }

    /** Serializes an object as compact JSON. */
    private static byte[] getMessage(Object o, ObjectMapper mapper) {
        try {
            return mapper.writer().without(SerializationFeature.INDENT_OUTPUT).writeValueAsBytes(o);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("object cannot be serialized as JSON", e);
        }
    }

    private SignatureData(byte[] bytes) {
        super(bytes);
    }
}
