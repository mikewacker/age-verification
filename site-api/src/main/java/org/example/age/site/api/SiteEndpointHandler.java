package org.example.age.site.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.CodeSender;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.common.api.data.AccountIdExtractor;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.data.DataMapper;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.infra.api.ExchangeCodeSender;
import org.example.age.infra.api.ExchangeDispatcher;
import org.example.age.infra.api.ExchangeJsonSender;
import org.example.age.infra.api.request.RequestParser;

@Singleton
final class SiteEndpointHandler implements HttpHandler {

    private static final ObjectMapper mapper = DataMapper.get();

    private final SiteApi siteApi;
    private final AccountIdExtractor accountIdExtractor;
    private final AuthMatchDataExtractor authDataExtractor;

    @Inject
    public SiteEndpointHandler(
            SiteApi siteApi, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        this.siteApi = siteApi;
        this.accountIdExtractor = accountIdExtractor;
        this.authDataExtractor = authDataExtractor;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        RequestParser parser = RequestParser.create(exchange, mapper);
        switch (exchange.getRelativePath()) {
            case "/verification-session" -> handleVerificationSession(exchange);
            case "/age-certificate" -> parser.parseBody(new TypeReference<>() {}, this::handleAgeCertificate);
            default -> ExchangeCodeSender.create(exchange).sendError(StatusCodes.NOT_FOUND);
        }
    }

    private void handleVerificationSession(HttpServerExchange exchange) {
        JsonSender<VerificationSession> sender = ExchangeJsonSender.create(exchange, mapper);

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

        Dispatcher dispatcher = ExchangeDispatcher.create(exchange);
        siteApi.createVerificationSession(sender, accountId, authData, dispatcher);
    }

    private void handleAgeCertificate(
            HttpServerExchange exchange, RequestParser parser, SignedAgeCertificate signedCertificate) {
        CodeSender sender = ExchangeCodeSender.create(exchange);

        Dispatcher dispatcher = ExchangeDispatcher.create(exchange);
        siteApi.processAgeCertificate(sender, signedCertificate, dispatcher);
    }
}
