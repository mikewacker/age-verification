package org.example.age.module.extractor.common.builtin;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;
import static org.mockito.Mockito.mock;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import javax.inject.Singleton;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.common.AuthMatchData;
import org.example.age.module.extractor.common.AuthMatchDataExtractor;
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
        HttpServerExchange exchange = createStubExchange();
        HttpOptional<AuthMatchData> maybeAuthData = authDataExtractor.tryExtract(exchange);
        AuthMatchData expectedAuthData = DisabledAuthMatchData.of();
        assertThat(maybeAuthData).hasValue(expectedAuthData);
    }

    private static HttpServerExchange createStubExchange() {
        return mock(HttpServerExchange.class);
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
