package org.example.age.test.service.data;

import static org.example.age.testing.api.HttpOptionalAssert.assertThat;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import java.util.Map;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AccountIdExtractor;
import org.example.age.testing.exchange.StubExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class TestAccountIdExtractorTest {

    private static AccountIdExtractor accountIdExtractor;

    @BeforeAll
    public static void createAccountIdExtractor() {
        accountIdExtractor = TestComponent.createAccountIdExtractor();
    }

    @Test
    public void extract_HeaderPresent() {
        HttpServerExchange exchange = StubExchanges.create(Map.of("Account-Id", "username"));
        HttpOptional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        assertThat(maybeAccountId).hasValue("username");
    }

    @Test
    public void extractEmpty_HeaderNotPresent() {
        HttpServerExchange exchange = StubExchanges.create(Map.of());
        HttpOptional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        assertThat(maybeAccountId).isEmptyWithErrorCode(401);
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
