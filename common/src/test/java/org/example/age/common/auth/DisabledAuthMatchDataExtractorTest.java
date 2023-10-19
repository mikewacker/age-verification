package org.example.age.common.auth;

import static org.mockito.Mockito.mock;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import javax.inject.Singleton;
import org.example.age.data.certificate.AuthKey;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class DisabledAuthMatchDataExtractorTest {

    private static AuthMatchDataExtractor extractor;
    private static AuthKey key;

    @BeforeAll
    public static void createExtractorAndKey() {
        extractor = TestComponent.createAuthMatchDataExtractor();
        key = mock(AuthKey.class);
    }

    @Test
    public void match() {
        HttpServerExchange localExchange = createStubExchange();
        HttpServerExchange remoteExchange = createStubExchange();
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, true);
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
