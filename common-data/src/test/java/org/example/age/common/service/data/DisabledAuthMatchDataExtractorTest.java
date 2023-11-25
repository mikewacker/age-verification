package org.example.age.common.service.data;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.testing.exchange.StubExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class DisabledAuthMatchDataExtractorTest {

    private static AuthMatchDataExtractor authDataExtractor;

    @BeforeAll
    public static void createAuthMatchDataExtractor() {
        authDataExtractor = TestComponent.createAuthMatchDataExtractor();
    }

    @Test
    public void extract() {
        HttpServerExchange exchange = StubExchanges.create();
        HttpOptional<AuthMatchData> maybeAuthData = authDataExtractor.tryExtract(exchange);
        AuthMatchData expectedAuthData = DisabledAuthMatchData.of();
        assertThat(maybeAuthData).hasValue(expectedAuthData);
    }

    /** Dagger component that provides an {@link AuthMatchDataExtractor}. */
    @Component(modules = DisabledAuthMatchDataExtractorModule.class)
    @Singleton
    interface TestComponent {

        static AuthMatchDataExtractor createAuthMatchDataExtractor() {
            TestComponent component = DaggerDisabledAuthMatchDataExtractorTest_TestComponent.create();
            return component.authMatchDataExtractor();
        }

        AuthMatchDataExtractor authMatchDataExtractor();
    }
}
