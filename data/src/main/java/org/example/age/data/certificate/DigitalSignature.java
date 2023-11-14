package org.example.age.data.certificate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.example.age.data.DataMapper;
import org.example.age.data.crypto.SignatureUtils;
import org.example.age.data.internal.ImmutableBytes;
import org.example.age.data.internal.StaticFromStringDeserializer;

/**
 * Signature used to verify the sender's identity.
 *
 * <p>The value to sign must be serializable using {@link DataMapper}.</p>
 */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = DigitalSignature.Deserializer.class)
public final class DigitalSignature extends ImmutableBytes {

    /** Creates a signature from a copy of the raw bytes. */
    public static DigitalSignature ofBytes(byte[] rawSignature) {
        return new DigitalSignature(rawSignature);
    }

    /** Creates a signature from URL-friendly base64 text. */
    public static DigitalSignature fromString(String rawSignature) {
        return new DigitalSignature(rawSignature);
    }

    /** Signs the value. */
    public static DigitalSignature sign(Object value, PrivateKey privateKey) {
        byte[] rawValue = serialize(value);
        byte[] rawSignature = SignatureUtils.sign(rawValue, privateKey);
        return ofBytes(rawSignature);
    }

    /** Verifies the signature against the value, returning whether verification succeeded. */
    public boolean verify(Object value, PublicKey publicKey) {
        byte[] rawValue = serialize(value);
        return SignatureUtils.verify(rawValue, bytes, publicKey);
    }

    private DigitalSignature(byte[] rawSignature) {
        super(rawSignature);
    }

    private DigitalSignature(String rawSignature) {
        super(rawSignature);
    }

    /** Serializes the value using {@link DataMapper}. */
    private static byte[] serialize(Object value) {
        try {
            return DataMapper.get().writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends StaticFromStringDeserializer<DigitalSignature> {

        public Deserializer() {
            super(DigitalSignature.class, DigitalSignature::fromString);
        }
    }
}
