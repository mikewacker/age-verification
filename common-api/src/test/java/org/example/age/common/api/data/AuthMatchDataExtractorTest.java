package org.example.age.common.api.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import org.example.age.api.Sender;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.BytesValue;
import org.example.age.testing.api.FakeCodeSender;
import org.immutables.value.Value;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class AuthMatchDataExtractorTest {

    private static AuthMatchDataExtractor dataExtractor;

    private static Aes256Key key;
    private FakeCodeSender sender;

    @BeforeAll
    public static void createAuthMatchDataExtractorEtAl() {
        dataExtractor = TestAuthMatchDataExtractor.create();
        key = Aes256Key.generate();
    }

    @BeforeEach
    public void createSender() {
        sender = FakeCodeSender.create();
    }

    @Test
    public void encryptThenDecrypt() {
        AuthMatchData data = TestAuthMatchData.of("data");
        AesGcmEncryptionPackage token = dataExtractor.encrypt(data, key);
        Optional<AuthMatchData> maybeRtData = dataExtractor.tryDecrypt(token, key, sender);
        assertThat(maybeRtData).hasValue(data);
        assertThat(sender.tryGet()).isEmpty();
    }

    @Test
    public void decryptFailed_Decryption() {
        AesGcmEncryptionPackage token = AesGcmEncryptionPackage.of(BytesValue.empty(), BytesValue.empty());
        Optional<AuthMatchData> maybeRtData = dataExtractor.tryDecrypt(token, key, sender);
        assertThat(maybeRtData).isEmpty();
        assertThat(sender.tryGet()).hasValue(401);
    }

    @Test
    public void decryptFailed_Deserialization() {
        byte[] rawData = new byte[4];
        AesGcmEncryptionPackage token = AesGcmEncryptionPackage.encrypt(rawData, key);
        Optional<AuthMatchData> maybeRtData = dataExtractor.tryDecrypt(token, key, sender);
        assertThat(maybeRtData).isEmpty();
        assertThat(sender.tryGet()).hasValue(400);
    }

    /** Test {@link AuthMatchDataExtractor}. */
    private static final class TestAuthMatchDataExtractor extends AuthMatchDataExtractor {

        public static AuthMatchDataExtractor create() {
            return new TestAuthMatchDataExtractor();
        }

        @Override
        public Optional<AuthMatchData> tryExtract(HttpServerExchange exchange, Sender sender) {
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
