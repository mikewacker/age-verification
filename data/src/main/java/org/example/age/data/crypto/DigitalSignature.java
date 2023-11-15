package org.example.age.data.crypto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.example.age.data.internal.ImmutableBytes;
import org.example.age.data.internal.StaticFromStringDeserializer;

/** Signature used to verify the sender's identity. */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = DigitalSignature.Deserializer.class)
public final class DigitalSignature extends ImmutableBytes {

    /** Creates a signature from a copy of the raw bytes. */
    public static DigitalSignature ofBytes(byte[] rawSignature) {
        return new DigitalSignature(rawSignature);
    }

    /** Deserializes a signature from URL-friendly base64 text. */
    public static DigitalSignature fromString(String rawSignature) {
        return new DigitalSignature(rawSignature);
    }

    /** Signs the message. */
    public static DigitalSignature sign(byte[] rawMessage, PrivateKey privateKey) {
        byte[] rawSignature = SignatureUtils.sign(rawMessage, privateKey);
        return ofBytes(rawSignature);
    }

    /** Verifies the signature against the message, returning whether verification succeeded. */
    public boolean verify(byte[] rawMessage, PublicKey publicKey) {
        return SignatureUtils.verify(rawMessage, bytes, publicKey);
    }

    private DigitalSignature(byte[] rawSignature) {
        super(rawSignature);
    }

    private DigitalSignature(String rawSignature) {
        super(rawSignature);
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<DigitalSignature> {

        public Deserializer() {
            super(DigitalSignature.class, DigitalSignature::fromString);
        }
    }
}
