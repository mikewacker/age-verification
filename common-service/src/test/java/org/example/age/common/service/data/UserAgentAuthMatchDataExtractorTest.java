package org.example.age.common.service.data;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import dagger.Component;
import dagger.Module;
import io.undertow.server.HttpServerExchange;
import java.util.Map;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.testing.exchange.StubExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class UserAgentAuthMatchDataExtractorTest {

    private static AuthMatchDataExtractor dataExtractor;

    private static Aes256Key key;

    @BeforeAll
    public static void createAuthMatchDataExtractorEtAl() {
        dataExtractor = TestComponent.createAuthMatchDataExtractor();
        key = Aes256Key.generate();
    }

    @Test
    public void extract() {
        HttpServerExchange exchange = StubExchanges.create(Map.of("User-Agent", "agent"));
        HttpOptional<AuthMatchData> maybeData = dataExtractor.tryExtract(exchange);
        AuthMatchData expectedData = UserAgentAuthMatchData.of("agent");
        assertThat(maybeData).hasValue(expectedData);
    }

    @Test
    public void extractFailed() {
        HttpServerExchange exchange = StubExchanges.create(Map.of());
        HttpOptional<AuthMatchData> maybeData = dataExtractor.tryExtract(exchange);
        AuthMatchData expectedData = UserAgentAuthMatchData.of("");
        assertThat(maybeData).hasValue(expectedData);
    }

    @Test
    public void encryptThenDecrypt() {
        AuthMatchData data = UserAgentAuthMatchData.of("agent");
        AesGcmEncryptionPackage token = dataExtractor.encrypt(data, key);
        HttpOptional<AuthMatchData> maybeRtData = dataExtractor.tryDecrypt(token, key);
        assertThat(maybeRtData).hasValue(data);
    }

    /** Dagger module that binds dependencies for {@link AuthMatchDataExtractor}. */
    @Module(includes = {UserAgentAuthMatchDataExtractorModule.class, DataMapperModule.class})
    interface TestModule {}

    /** Dagger component that provides an {@link AuthMatchDataExtractor}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static AuthMatchDataExtractor createAuthMatchDataExtractor() {
            TestComponent component = DaggerUserAgentAuthMatchDataExtractorTest_TestComponent.create();
            return component.authMatchDataExtractor();
        }

        AuthMatchDataExtractor authMatchDataExtractor();
    }
}
