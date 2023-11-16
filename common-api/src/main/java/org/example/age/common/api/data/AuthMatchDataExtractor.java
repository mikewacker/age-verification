package org.example.age.common.api.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.util.Optional;
import org.example.age.api.HttpOptional;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

/**
 * Extracts {@link AuthMatchData} from an {@link HttpServerExchange}, or returns an error status code.
 *
 * <p>Also encrypts and decrypts the {@link AuthMatchData}.</p>
 */
public abstract class AuthMatchDataExtractor {

    private final ObjectMapper mapper;
    private final TypeReference<? extends AuthMatchData> dataTypeRef;

    /** Extracts {@link AuthMatchData} from an {@link HttpServerExchange}, or returns an error status code. */
    public abstract HttpOptional<AuthMatchData> tryExtract(HttpServerExchange exchange);

    /** Encrypts {@link AuthMatchData}. */
    public final AesGcmEncryptionPackage encrypt(AuthMatchData data, Aes256Key key) {
        byte[] rawData = serialize(data);
        return AesGcmEncryptionPackage.encrypt(rawData, key);
    }

    /** Decrypts {@link AuthMatchData}, or returns an error status code. */
    public final HttpOptional<AuthMatchData> tryDecrypt(AesGcmEncryptionPackage token, Aes256Key key) {
        Optional<byte[]> maybeRawData = token.tryDecrypt(key);
        if (maybeRawData.isEmpty()) {
            return HttpOptional.empty(StatusCodes.UNAUTHORIZED);
        }
        byte[] rawData = maybeRawData.get();

        return tryDeserialize(rawData);
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

    /** Deserializes {@link AuthMatchData}, or returns a 400 error. */
    private HttpOptional<AuthMatchData> tryDeserialize(byte[] rawData) {
        try {
            AuthMatchData data = mapper.readValue(rawData, dataTypeRef);
            return HttpOptional.of(data);
        } catch (IOException e) {
            return HttpOptional.empty(StatusCodes.BAD_REQUEST);
        }
    }
}
