package org.example.age.avs.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.api.CodeSender;
import org.example.age.common.api.ExchangeExecutors;
import org.example.age.common.api.JsonSender;
import org.example.age.common.api.data.account.AccountIdExtractor;
import org.example.age.common.api.data.auth.AuthMatchData;
import org.example.age.common.api.data.auth.AuthMatchDataExtractor;
import org.example.age.common.api.request.impl.RequestParser;
import org.example.age.data.DataMapper;
import org.example.age.data.SecureId;
import org.example.age.data.certificate.VerificationSession;

@Singleton
final class AvsEndpointHandler implements HttpHandler {

    private static final ObjectMapper mapper = DataMapper.get();

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
        RequestParser parser = RequestParser.create(exchange, mapper);
        switch (exchange.getRelativePath()) {
            case "/verification-session" -> handleVerificationSession(exchange, parser);
            case "/linked-verification-request" -> handleLinkedVerificationRequest(exchange, parser);
            case "/age-certificate" -> handleAgeCertificate(exchange);
            default -> CodeSender.create(exchange).send(StatusCodes.NOT_FOUND);
        }
    }

    private void handleVerificationSession(HttpServerExchange exchange, RequestParser parser) {
        JsonSender<VerificationSession> sender = JsonSender.create(exchange, mapper);

        Optional<String> maybeSiteId = parser.tryGetQueryParameter("site-id");
        if (maybeSiteId.isEmpty()) {
            return;
        }
        String siteId = maybeSiteId.get();

        ExchangeExecutors executors = ExchangeExecutors.create(exchange);
        avsApi.createVerificationSession(sender, siteId, executors);
    }

    private void handleLinkedVerificationRequest(HttpServerExchange exchange, RequestParser parser) {
        CodeSender sender = CodeSender.create(exchange);

        Optional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange, sender);
        if (maybeAccountId.isEmpty()) {
            return;
        }
        String accountId = maybeAccountId.get();

        Optional<SecureId> maybeRequestId = parser.tryGetQueryParameter("request-id", new TypeReference<>() {});
        if (maybeRequestId.isEmpty()) {
            return;
        }
        SecureId requestId = maybeRequestId.get();

        ExchangeExecutors executors = ExchangeExecutors.create(exchange);
        avsApi.linkVerificationRequest(sender, accountId, requestId, executors);
    }

    private void handleAgeCertificate(HttpServerExchange exchange) {
        CodeSender sender = CodeSender.create(exchange);

        Optional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange, sender);
        if (maybeAccountId.isEmpty()) {
            return;
        }
        String accountId = maybeAccountId.get();

        Optional<AuthMatchData> maybeAuthData = authDataExtractor.tryExtract(exchange, sender);
        if (maybeAuthData.isEmpty()) {
            return;
        }
        AuthMatchData authData = maybeAuthData.get();

        ExchangeExecutors executors = ExchangeExecutors.create(exchange);
        avsApi.sendAgeCertificate(sender, accountId, authData, executors);
    }
}
