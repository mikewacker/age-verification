package org.example.age.site.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.Dispatcher;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSender;
import org.example.age.api.JsonSerializer;
import org.example.age.api.StatusCodeSender;
import org.example.age.common.api.data.AccountIdExtractor;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.infra.api.ExchangeDispatcher;
import org.example.age.infra.api.ExchangeJsonSender;
import org.example.age.infra.api.ExchangeStatusCodeSender;
import org.example.age.infra.api.RequestParser;

@Singleton
final class SiteEndpointHandler implements HttpHandler {

    private final SiteApi siteApi;
    private final AccountIdExtractor accountIdExtractor;
    private final AuthMatchDataExtractor authDataExtractor;
    private final JsonSerializer serializer;

    @Inject
    public SiteEndpointHandler(
            SiteApi siteApi,
            AccountIdExtractor accountIdExtractor,
            AuthMatchDataExtractor authDataExtractor,
            @Named("api") JsonSerializer serializer) {
        this.siteApi = siteApi;
        this.accountIdExtractor = accountIdExtractor;
        this.authDataExtractor = authDataExtractor;
        this.serializer = serializer;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        RequestParser parser = RequestParser.create(exchange, serializer);
        switch (exchange.getRelativePath()) {
            case "/verification-session" -> handleVerificationSession(exchange);
            case "/age-certificate" -> parser.readBody(new TypeReference<>() {}, this::handleAgeCertificate);
            default -> ExchangeStatusCodeSender.create(exchange).sendErrorCode(StatusCodes.NOT_FOUND);
        }
    }

    private void handleVerificationSession(HttpServerExchange exchange) {
        JsonSender<VerificationSession> sender = ExchangeJsonSender.create(exchange, serializer);

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
        siteApi.createVerificationSession(sender, accountId, authData, dispatcher);
    }

    private void handleAgeCertificate(
            HttpServerExchange exchange, RequestParser parser, SignedAgeCertificate signedCertificate) {
        StatusCodeSender sender = ExchangeStatusCodeSender.create(exchange);

        Dispatcher dispatcher = ExchangeDispatcher.create(exchange);
        siteApi.processAgeCertificate(sender, signedCertificate, dispatcher);
    }
}
