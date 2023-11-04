package org.example.age.testing.service.data.account;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.api.data.account.AccountIdExtractor;

/** Extracts an account ID from the custom {@code Account-Id} header. */
@Singleton
final class TestAccountIdExtractor implements AccountIdExtractor {

    @Inject
    public TestAccountIdExtractor() {}

    @Override
    public Optional<String> tryExtract(HttpServerExchange exchange) {
        return Optional.ofNullable(exchange.getRequestHeaders().getFirst("Account-Id"));
    }
}
