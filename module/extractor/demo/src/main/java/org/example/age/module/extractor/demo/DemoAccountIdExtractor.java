package org.example.age.module.extractor.demo;

import io.github.mikewacker.drift.api.HttpOptional;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.Optional;
import org.example.age.api.extractor.AccountIdExtractor;

/** Extracts an account ID from the custom {@code Account-Id} header, or sends a 401 error. */
@Singleton
final class DemoAccountIdExtractor implements AccountIdExtractor {

    @Inject
    public DemoAccountIdExtractor() {}

    @Override
    public HttpOptional<String> tryExtract(HttpServerExchange exchange) {
        Optional<String> maybeAccountId =
                Optional.ofNullable(exchange.getRequestHeaders().getFirst("Account-Id"));
        return HttpOptional.fromOptional(maybeAccountId, StatusCodes.UNAUTHORIZED);
    }
}
