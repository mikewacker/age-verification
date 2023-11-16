package org.example.age.common.api.data;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.undertow.server.HttpServerExchange;
import org.example.age.api.HttpOptional;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.BytesValue;
import org.immutables.value.Value;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class AuthMatchDataExtractorTest {

    private static AuthMatchDataExtractor dataExtractor;

    private static Aes256Key key;

    @BeforeAll
    public static void createAuthMatchDataExtractorEtAl() {
        dataExtractor = TestAuthMatchDataExtractor.create();
        key = Aes256Key.generate();
    }

    @Test
    public void encryptThenDecrypt() {
        AuthMatchData data = TestAuthMatchData.of("data");
        AesGcmEncryptionPackage token = dataExtractor.encrypt(data, key);
        HttpOptional<AuthMatchData> maybeRtData = dataExtractor.tryDecrypt(token, key);
        assertThat(maybeRtData).hasValue(data);
    }

    @Test
    public void decryptFailed_Decryption() {
        AesGcmEncryptionPackage token = AesGcmEncryptionPackage.of(BytesValue.empty(), BytesValue.empty());
        HttpOptional<AuthMatchData> maybeRtData = dataExtractor.tryDecrypt(token, key);
        assertThat(maybeRtData).isEmptyWithStatusCode(401);
    }

    @Test
    public void decryptFailed_Deserialization() {
        byte[] rawData = new byte[4];
        AesGcmEncryptionPackage token = AesGcmEncryptionPackage.encrypt(rawData, key);
        HttpOptional<AuthMatchData> maybeRtData = dataExtractor.tryDecrypt(token, key);
        assertThat(maybeRtData).isEmptyWithStatusCode(400);
    }

    /** Test {@link AuthMatchDataExtractor}. */
    private static final class TestAuthMatchDataExtractor extends AuthMatchDataExtractor {

        public static AuthMatchDataExtractor create() {
            return new TestAuthMatchDataExtractor();
        }

        @Override
        public HttpOptional<AuthMatchData> tryExtract(HttpServerExchange exchange) {
            throw new UnsupportedOperationException();
        }

        private TestAuthMatchDataExtractor() {
            super(new ObjectMapper(), new TypeReference<TestAuthMatchData>() {});
        }
    }

    /** Test {@link AuthMatchData}. */
    @Value.Immutable
    @JsonSerialize(as = ImmutableTestAuthMatchData.class)
    @JsonDeserialize(as = ImmutableTestAuthMatchData.class)
    public interface TestAuthMatchData extends AuthMatchData {

        static AuthMatchData of(String data) {
            return ImmutableTestAuthMatchData.builder().data(data).build();
        }

        String data();

        @Override
        default boolean match(AuthMatchData other) {
            return equals(other);
        }
    }
}
