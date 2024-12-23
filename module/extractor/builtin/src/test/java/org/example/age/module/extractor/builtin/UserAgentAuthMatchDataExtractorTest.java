package org.example.age.module.extractor.builtin;

import static io.github.mikewacker.drift.testing.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dagger.Component;
import io.github.mikewacker.drift.api.HttpOptional;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.example.age.api.def.AuthMatchData;
import org.example.age.api.extractor.AuthMatchDataExtractor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class UserAgentAuthMatchDataExtractorTest {

    private static AuthMatchDataExtractor authDataExtractor;

    @BeforeAll
    public static void createAuthMatchDataExtractorEtAl() {
        authDataExtractor = TestComponent.createAuthMatchDataExtractor();
    }

    @Test
    public void extract_HeaderPresent() {
        HttpServerExchange exchange = createStubExchange(Optional.of("agent"));
        HttpOptional<AuthMatchData> maybeAuthData = authDataExtractor.tryExtract(exchange);
        AuthMatchData expectedAuthData = UserAgentAuthMatchData.of("agent");
        assertThat(maybeAuthData).hasValue(expectedAuthData);
    }

    @Test
    public void extract_HeaderNotPresent() {
        HttpServerExchange exchange = createStubExchange(Optional.empty());
        HttpOptional<AuthMatchData> maybeAuthData = authDataExtractor.tryExtract(exchange);
        AuthMatchData expectedAuthData = UserAgentAuthMatchData.of("");
        assertThat(maybeAuthData).hasValue(expectedAuthData);
    }

    private static HttpServerExchange createStubExchange(Optional<String> maybeUserAgent) {
        HeaderMap requestHeaderMap = new HeaderMap();
        maybeUserAgent.ifPresent(userAgent -> requestHeaderMap.put(Headers.USER_AGENT, userAgent));

        HttpServerExchange exchange = mock(HttpServerExchange.class);
        when(exchange.getRequestHeaders()).thenReturn(requestHeaderMap);
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
