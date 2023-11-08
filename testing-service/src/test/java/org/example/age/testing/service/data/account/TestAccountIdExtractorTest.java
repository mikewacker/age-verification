package org.example.age.testing.service.data.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.common.api.data.account.AccountIdExtractor;
import org.example.age.testing.exchange.TestExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class TestAccountIdExtractorTest {

    private static AccountIdExtractor accountIdExtractor;

    @BeforeAll
    private static void createAccountIdExtractor() {
        accountIdExtractor = TestComponent.createAccountIdExtractor();
    }

    @Test
    public void extractAccountId() {
        HttpServerExchange exchange = createStubExchange(Optional.of("username"));
        Optional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        assertThat(maybeAccountId).hasValue("username");
    }

    @Test
    public void extractNothing() {
        HttpServerExchange exchange = createStubExchange(Optional.empty());
        Optional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        assertThat(maybeAccountId).isEmpty();
    }

    private static HttpServerExchange createStubExchange(Optional<String> maybeAccountId) {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        Map<HttpString, String> headers = new HashMap<>();
        maybeAccountId.ifPresent(accountId -> headers.put(HttpString.tryFromString("Account-Id"), accountId));
        TestExchanges.addRequestHeaders(exchange, headers);
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
