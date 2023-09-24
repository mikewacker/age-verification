package org.example.age.common.verification.auth;

import static org.mockito.Mockito.mock;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import javax.inject.Singleton;
import org.example.age.certificate.AuthKey;
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
        HttpServerExchange localExchange = mock(HttpServerExchange.class);
        HttpServerExchange remoteExchange = mock(HttpServerExchange.class);
        AuthMatchDataExtractorTestTemplate.match(extractor, key, localExchange, remoteExchange, true);
    }

    @Component(modules = DisabledAuthMatchModule.class)
    @Singleton
    interface TestComponent {

        static AuthMatchDataExtractor createAuthMatchDataExtractor() {
            TestComponent component = DaggerDisabledAuthMatchDataExtractorTest_TestComponent.create();
            return component.authMatchDataExtractor();
        }

        AuthMatchDataExtractor authMatchDataExtractor();
    }
}
