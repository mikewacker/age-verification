package org.example.age.testing.service.data;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.Sender;
import org.example.age.common.api.data.AccountIdExtractor;

/** Extracts an account ID from the custom {@code Account-Id} header, or sends a 401 error. */
@Singleton
final class TestAccountIdExtractor implements AccountIdExtractor {

    @Inject
    public TestAccountIdExtractor() {}

    @Override
    public Optional<String> tryExtract(HttpServerExchange exchange, Sender sender) {
        Optional<String> maybeAccountId =
                Optional.ofNullable(exchange.getRequestHeaders().getFirst("Account-Id"));
        if (maybeAccountId.isEmpty()) {
            sender.sendError(StatusCodes.UNAUTHORIZED);
        }
        return maybeAccountId;
    }
}
