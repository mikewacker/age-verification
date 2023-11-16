package org.example.age.common.api.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.util.Optional;
import org.example.age.api.Sender;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

/**
 * Extracts {@link AuthMatchData} from an {@link HttpServerExchange}, or sends an error status code.
 *
 * <p>Also encrypts and decrypts the {@link AuthMatchData}.</p>
 */
public abstract class AuthMatchDataExtractor {

    private final ObjectMapper mapper;
    private final TypeReference<? extends AuthMatchData> dataTypeRef;

    /** Extracts {@link AuthMatchData} from an {@link HttpServerExchange}, or sends an error status code. */
    public abstract Optional<AuthMatchData> tryExtract(HttpServerExchange exchange, Sender sender);

    /** Encrypts {@link AuthMatchData}. */
    public final AesGcmEncryptionPackage encrypt(AuthMatchData data, Aes256Key key) {
        byte[] rawData = serialize(data);
        return AesGcmEncryptionPackage.encrypt(rawData, key);
    }

    /** Decrypts {@link AuthMatchData}, or sends an error status code. */
    public final Optional<AuthMatchData> tryDecrypt(AesGcmEncryptionPackage token, Aes256Key key, Sender sender) {
        Optional<byte[]> maybeRawData = token.tryDecrypt(key);
        if (maybeRawData.isEmpty()) {
            sender.sendError(StatusCodes.UNAUTHORIZED);
            return Optional.empty();
        }
        byte[] rawData = maybeRawData.get();

        return tryDeserialize(rawData, sender);
    }

    protected AuthMatchDataExtractor(ObjectMapper mapper, TypeReference<? extends AuthMatchData> dataTypeRef) {
        this.mapper = mapper;
        this.dataTypeRef = dataTypeRef;
    }

    /** Serializes {@link AuthMatchData}. */
    private byte[] serialize(AuthMatchData data) {
        try {
            return mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("serialization failed", e);
        }
    }

    /** Deserializes {@link AuthMatchData}, or sends a 400 error. */
    private Optional<AuthMatchData> tryDeserialize(byte[] rawData, Sender sender) {
        try {
            AuthMatchData data = mapper.readValue(rawData, dataTypeRef);
            return Optional.of(data);
        } catch (IOException e) {
            sender.sendError(StatusCodes.BAD_REQUEST);
            return Optional.empty();
        }
    }
}
