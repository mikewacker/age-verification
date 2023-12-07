package org.example.age.data.crypto;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Signature used to verify the sender's identity.
 *
 * <p>Only {@code Ed25519} keys are supported.</p>
 */
public final class DigitalSignature extends ImmutableBytes {

    /** Creates a signature from a copy of the bytes. */
    public static DigitalSignature ofBytes(byte[] signature) {
        return new DigitalSignature(signature, true);
    }

    /** Signs the message. */
    public static DigitalSignature sign(byte[] message, PrivateKey privateKey) {
        byte[] signature = SignatureUtils.sign(message, privateKey);
        return ofUncopiedBytes(signature);
    }

    /** Verifies the signature against the message, returning whether verification succeeded. */
    public boolean verify(byte[] message, PublicKey publicKey) {
        return SignatureUtils.verify(message, uncopiedBytes(), publicKey);
    }

    /** Creates a signature from the bytes. */
    @JsonCreator
    static DigitalSignature ofUncopiedBytes(byte[] signature) {
        return new DigitalSignature(signature, false);
    }

    private DigitalSignature(byte[] signature, boolean copy) {
        super(signature, copy);
    }
}
