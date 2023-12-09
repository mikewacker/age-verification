package org.example.age.avs.api.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.StatusCodeSender;
import org.example.age.api.base.ValueSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.VerificationState;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.infra.api.ExchangeDispatcher;
import org.example.age.infra.api.ExchangeJsonSender;
import org.example.age.infra.api.ExchangeStatusCodeSender;
import org.example.age.infra.api.RequestParser;
import org.example.age.module.extractor.common.AccountIdExtractor;
import org.example.age.module.extractor.common.AuthMatchDataExtractor;

@Singleton
final class AvsEndpointHandler implements HttpHandler {

    private final AvsApi avsApi;
    private final AccountIdExtractor accountIdExtractor;
    private final AuthMatchDataExtractor authDataExtractor;

    @Inject
    public AvsEndpointHandler(
            AvsApi avsApi, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        this.avsApi = avsApi;
        this.accountIdExtractor = accountIdExtractor;
        this.authDataExtractor = authDataExtractor;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        RequestParser parser = RequestParser.create(exchange);
        switch (exchange.getRelativePath()) {
            case "/verification-state" -> handleVerificationState(exchange);
            case "/verification-session" -> handleVerificationSession(exchange, parser);
            case "/linked-verification-request" -> handleLinkedVerificationRequest(exchange, parser);
            case "/age-certificate" -> handleAgeCertificate(exchange);
            default -> ExchangeStatusCodeSender.create(exchange).sendErrorCode(StatusCodes.NOT_FOUND);
        }
    }

    private void handleVerificationState(HttpServerExchange exchange) {
        ValueSender<VerificationState> sender = ExchangeJsonSender.create(exchange);

        HttpOptional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        if (maybeAccountId.isEmpty()) {
            sender.sendErrorCode(maybeAccountId.statusCode());
            return;
        }
        String accountId = maybeAccountId.get();

        Dispatcher dispatcher = ExchangeDispatcher.create(exchange);
        avsApi.getVerificationState(sender, accountId, dispatcher);
    }

    private void handleVerificationSession(HttpServerExchange exchange, RequestParser parser) {
        ValueSender<VerificationSession> sender = ExchangeJsonSender.create(exchange);

        HttpOptional<String> maybeSiteId = parser.tryGetQueryParameter("site-id");
        if (maybeSiteId.isEmpty()) {
            sender.sendErrorCode(maybeSiteId.statusCode());
            return;
        }
        String siteId = maybeSiteId.get();

        Dispatcher dispatcher = ExchangeDispatcher.create(exchange);
        avsApi.createVerificationSession(sender, siteId, dispatcher);
    }

    private void handleLinkedVerificationRequest(HttpServerExchange exchange, RequestParser parser) {
        StatusCodeSender sender = ExchangeStatusCodeSender.create(exchange);

        HttpOptional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        if (maybeAccountId.isEmpty()) {
            sender.sendErrorCode(maybeAccountId.statusCode());
            return;
        }
        String accountId = maybeAccountId.get();

        HttpOptional<SecureId> maybeRequestId = parser.tryGetQueryParameter("request-id", new TypeReference<>() {});
        if (maybeRequestId.isEmpty()) {
            sender.sendErrorCode(maybeRequestId.statusCode());
            return;
        }
        SecureId requestId = maybeRequestId.get();

        Dispatcher dispatcher = ExchangeDispatcher.create(exchange);
        avsApi.linkVerificationRequest(sender, accountId, requestId, dispatcher);
    }

    private void handleAgeCertificate(HttpServerExchange exchange) {
        StatusCodeSender sender = ExchangeStatusCodeSender.create(exchange);

        HttpOptional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        if (maybeAccountId.isEmpty()) {
            sender.sendErrorCode(maybeAccountId.statusCode());
            return;
        }
        String accountId = maybeAccountId.get();

        HttpOptional<AuthMatchData> maybeAuthData = authDataExtractor.tryExtract(exchange);
        if (maybeAuthData.isEmpty()) {
            sender.sendErrorCode(maybeAuthData.statusCode());
            return;
        }
        AuthMatchData authData = maybeAuthData.get();

        Dispatcher dispatcher = ExchangeDispatcher.create(exchange);
        avsApi.sendAgeCertificate(sender, accountId, authData, dispatcher);
    }
}
