package org.example.age.common.service.data;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import java.util.Map;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.testing.exchange.StubExchanges;
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
        AuthMatchData expectedAuthData = UserAgentAuthMatchData.of("agent");
        extract(Map.of("User-Agent", "agent"), expectedAuthData);
    }

    @Test
    public void extract_HeaderNotPresent() {
        AuthMatchData expectedAuthData = UserAgentAuthMatchData.of("");
        extract(Map.of(), expectedAuthData);
    }

    private void extract(Map<String, String> headers, AuthMatchData expectedAuthData) {
        HttpServerExchange exchange = StubExchanges.create(headers);
        HttpOptional<AuthMatchData> maybeAuthData = authDataExtractor.tryExtract(exchange);
        assertThat(maybeAuthData).hasValue(expectedAuthData);
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
