package org.example.age.common.verification.auth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import javax.inject.Singleton;
import org.example.age.certificate.AuthKey;
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
        HttpServerExchange localExchange = createExchange("user agent");
        HttpServerExchange remoteExchange = createExchange("user agent");
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, true);
    }

    @Test
    public void match_UserAgentsDoNotMatch() {
        HttpServerExchange localExchange = createExchange("user agent 1");
        HttpServerExchange remoteExchange = createExchange("user agent 2");
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, false);
    }

    @Test
    public void match_UserAgentNotPresent() {
        HttpServerExchange localExchange = createExchange(null);
        HttpServerExchange remoteExchange = createExchange(null);
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, true);
    }

    private static HttpServerExchange createExchange(String userAgent) {
        HeaderMap headerMap = new HeaderMap();
        headerMap.put(Headers.USER_AGENT, userAgent);
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        when(exchange.getRequestHeaders()).thenReturn(headerMap);
        return exchange;
    }

    @Component(modules = UserAgentAuthMatchModule.class)
    @Singleton
    interface TestComponent {

        static AuthMatchDataExtractor createAuthMatchDataExtractor() {
            TestComponent component = DaggerUserAgentAuthMatchDataExtractorTest_TestComponent.create();
            return component.authMatchDataExtractor();
        }

        AuthMatchDataExtractor authMatchDataExtractor();
    }
}
