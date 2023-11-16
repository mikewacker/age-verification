package org.example.age.common.service.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.testing.api.FakeCodeSender;
import org.example.age.testing.exchange.StubExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class DisabledAuthMatchDataExtractorTest {

    private static AuthMatchDataExtractor dataExtractor;

    private static Aes256Key key;
    private FakeCodeSender sender;

    @BeforeAll
    public static void createAuthMatchDataExtractorEtAl() {
        dataExtractor = TestComponent.createAuthMatchDataExtractor();
        key = Aes256Key.generate();
    }

    @BeforeEach
    public void createSender() {
        sender = FakeCodeSender.create();
    }

    @Test
    public void extract() {
        HttpServerExchange exchange = StubExchanges.create();
        Optional<AuthMatchData> maybeData = dataExtractor.tryExtract(exchange, sender);
        assertThat(maybeData).isPresent();
        assertThat(sender.tryGet()).isEmpty();
    }

    @Test
    public void encryptThenDecrypt() {
        AuthMatchData data = DisabledAuthMatchData.of();
        AesGcmEncryptionPackage token = dataExtractor.encrypt(data, key);
        Optional<AuthMatchData> maybeRtData = dataExtractor.tryDecrypt(token, key, sender);
        assertThat(maybeRtData).hasValue(data);
        assertThat(sender.tryGet()).isEmpty();
    }

    /** Dagger module that binds dependencies for {@link AuthMatchDataExtractor}. */
    @Module(includes = DisabledAuthMatchDataExtractorModule.class)
    interface TestModule {

        @Provides
        @Singleton
        static ObjectMapper provideObjectMapper() {
            return new ObjectMapper();
        }
    }

    /** Dagger component that provides an {@link AuthMatchDataExtractor}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static AuthMatchDataExtractor createAuthMatchDataExtractor() {
            TestComponent component = DaggerDisabledAuthMatchDataExtractorTest_TestComponent.create();
            return component.authMatchDataExtractor();
        }

        AuthMatchDataExtractor authMatchDataExtractor();
    }
}
