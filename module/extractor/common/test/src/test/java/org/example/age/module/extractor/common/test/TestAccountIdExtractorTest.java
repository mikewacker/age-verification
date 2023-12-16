package org.example.age.module.extractor.common.test;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.module.extractor.common.AccountIdExtractor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class TestAccountIdExtractorTest {

    private static AccountIdExtractor accountIdExtractor;

    @BeforeAll
    public static void createAccountIdExtractor() {
        accountIdExtractor = TestComponent.createAccountIdExtractor();
    }

    @Test
    public void extract() {
        HttpServerExchange exchange = createStubExchange(Optional.of("username"));
        HttpOptional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        assertThat(maybeAccountId).hasValue("username");
    }

    @Test
    public void extractFailed() {
        HttpServerExchange exchange = createStubExchange(Optional.empty());
        HttpOptional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        assertThat(maybeAccountId).isEmptyWithErrorCode(401);
    }

    private static HttpServerExchange createStubExchange(Optional<String> maybeAccountId) {
        HeaderMap requestHeaderMap = new HeaderMap();
        maybeAccountId.ifPresent(userAgent -> requestHeaderMap.put(HttpString.tryFromString("Account-Id"), userAgent));

        HttpServerExchange exchange = mock(HttpServerExchange.class);
        when(exchange.getRequestHeaders()).thenReturn(requestHeaderMap);
        return exchange;
    }

    /** Dagger component that provides an {@link AccountIdExtractor}. */
    @Component(modules = TestAccountIdExtractorModule.class)
    @Singleton
    interface TestComponent {

        static AccountIdExtractor createAccountIdExtractor() {
            TestComponent component = DaggerTestAccountIdExtractorTest_TestComponent.create();
            return component.accountIdExtractor();
        }

        AccountIdExtractor accountIdExtractor();
    }
}
