package org.example.age.common.service.data;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import java.util.Map;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;
import org.example.age.testing.api.FakeCodeSender;
import org.example.age.testing.exchange.StubExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class UserAgentAuthMatchDataExtractorTest {

    private static AuthMatchDataExtractor extractor;
    private static AuthKey key;

    @BeforeAll
    public static void createAuthMatchDataExtractorEtAl() {
        extractor = TestComponent.createAuthMatchDataExtractor();
        key = AuthKey.generate();
    }

    @Test
    public void match_UserAgentsMatch() {
        HttpServerExchange localExchange = StubExchanges.create(Map.of("User-Agent", "agent"));
        HttpServerExchange remoteExchange = StubExchanges.create(Map.of("User-Agent", "agent"));
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, true);
    }

    @Test
    public void match_UserAgentsDoNotMatch() {
        HttpServerExchange localExchange = StubExchanges.create(Map.of("User-Agent", "agent1"));
        HttpServerExchange remoteExchange = StubExchanges.create(Map.of("User-Agent", "agent2"));
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, false);
    }

    @Test
    public void match_UserAgentNotPresent() {
        HttpServerExchange localExchange = StubExchanges.create(Map.of());
        HttpServerExchange remoteExchange = StubExchanges.create(Map.of());
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, true);
    }

    @Test
    public void sendError_DecryptionFails() {
        FakeCodeSender sender = FakeCodeSender.create();
        AuthToken token = AuthToken.empty();
        Optional<AuthMatchData> maybeData = extractor.tryDecrypt(token, key, sender);
        assertThat(maybeData).isEmpty();
        assertThat(sender.tryGet()).hasValue(401);
    }

    /** Dagger component that provides an {@link AuthMatchDataExtractor}. */
    @Component(modules = UserAgentAuthMatchDataExtractorModule.class)
    @Singleton
    interface TestComponent {

        static AuthMatchDataExtractor createAuthMatchDataExtractor() {
            TestComponent component = DaggerUserAgentAuthMatchDataExtractorTest_TestComponent.create();
            return component.authMatchDataExtractor();
        }

        AuthMatchDataExtractor authMatchDataExtractor();
    }
}
