package org.example.age.common.site.auth;

import static org.mockito.Mockito.mock;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.util.Map;
import javax.inject.Singleton;
import org.example.age.data.certificate.AuthKey;
import org.example.age.testing.TestExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class UserAgentAuthMatchDataExtractorTest {

    private static AuthMatchDataExtractor extractor;
    private static AuthKey key;

    @BeforeAll
    public static void createExtractorAndKey() {
        extractor = TestComponent.createAuthMatchDataExtractor();
        key = AuthKey.generate();
    }

    @Test
    public void match_UserAgentsMatch() {
        HttpServerExchange localExchange = createStubExchange("user agent");
        HttpServerExchange remoteExchange = createStubExchange("user agent");
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, true);
    }

    @Test
    public void match_UserAgentsDoNotMatch() {
        HttpServerExchange localExchange = createStubExchange("user agent 1");
        HttpServerExchange remoteExchange = createStubExchange("user agent 2");
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, false);
    }

    @Test
    public void match_UserAgentNotPresent() {
        HttpServerExchange localExchange = createStubExchange("");
        HttpServerExchange remoteExchange = createStubExchange("");
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, true);
    }

    private static HttpServerExchange createStubExchange(String userAgent) {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addRequestHeaders(exchange, Map.of(Headers.USER_AGENT, userAgent));
        return exchange;
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
