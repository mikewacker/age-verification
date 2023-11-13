package org.example.age.test.service.data;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import java.util.Map;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.common.api.data.AccountIdExtractor;
import org.example.age.testing.api.FakeCodeSender;
import org.example.age.testing.exchange.StubExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class TestAccountIdExtractorTest {

    private static AccountIdExtractor accountIdExtractor;
    private FakeCodeSender sender;

    @BeforeAll
    public static void createAccountIdExtractor() {
        accountIdExtractor = TestComponent.createAccountIdExtractor();
    }

    @BeforeEach
    public void createSender() {
        sender = FakeCodeSender.create();
    }

    @Test
    public void extractAccountId() {
        HttpServerExchange exchange = StubExchanges.create(Map.of("Account-Id", "username"));
        Optional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange, sender);
        assertThat(maybeAccountId).hasValue("username");
        assertThat(sender.tryGet()).isEmpty();
    }

    @Test
    public void sendError() {
        HttpServerExchange exchange = StubExchanges.create(Map.of());
        Optional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange, sender);
        assertThat(maybeAccountId).isEmpty();
        assertThat(sender.tryGet()).hasValue(401);
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
