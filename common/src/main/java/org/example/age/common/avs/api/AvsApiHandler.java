package org.example.age.common.avs.api;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.security.PrivateKey;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.common.avs.verification.internal.VerificationManager;
import org.example.age.common.base.account.AccountIdExtractor;
import org.example.age.common.base.client.internal.RequestDispatcher;
import org.example.age.common.base.utils.internal.ExchangeUtils;

/**
 * HTTP handler for the age verification service's API.
 *
 * <p>In a real implementation, calls to a site would use HTTPS.</p>
 */
@Singleton
@SuppressWarnings("UnusedVariable")
final class AvsApiHandler implements HttpHandler {

    private final AccountIdExtractor accountIdExtractor;
    private final VerificationManager verificationManager;
    private final RequestDispatcher requestDispatcher;
    private final Supplier<PrivateKey> privateSigningKeySupplier;

    @Inject
    public AvsApiHandler(
            AccountIdExtractor accountIdExtractor,
            VerificationManager verificationManager,
            RequestDispatcher requestDispatcher,
            @Named("signing") Supplier<PrivateKey> privateSigningKeySupplier) {
        this.accountIdExtractor = accountIdExtractor;
        this.verificationManager = verificationManager;
        this.requestDispatcher = requestDispatcher;
        this.privateSigningKeySupplier = privateSigningKeySupplier;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        // TODO: Implement.
        ExchangeUtils.sendStatusCode(exchange, StatusCodes.NOT_FOUND);
    }
}
