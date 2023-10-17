package org.example.age.common.testing;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.account.AccountIdExtractor;

/** Extracts an account ID from the custom {@code Account-Id} header. */
@Singleton
public class HeaderAccountIdExtractor implements AccountIdExtractor {

    @Inject
    public HeaderAccountIdExtractor() {}

    @Override
    public Optional<String> tryExtract(HttpServerExchange exchange) {
        return Optional.ofNullable(exchange.getRequestHeaders().getFirst("Account-Id"));
    }
}
