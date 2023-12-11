package org.example.age.api.site.endpoint;

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
import org.example.age.api.common.AuthMatchData;
import org.example.age.api.common.VerificationState;
import org.example.age.api.infra.RequestParser;
import org.example.age.api.infra.UndertowDispatcher;
import org.example.age.api.infra.UndertowJsonValueSender;
import org.example.age.api.infra.UndertowStatusCodeSender;
import org.example.age.api.site.SiteApi;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.module.extractor.common.AccountIdExtractor;
import org.example.age.module.extractor.common.AuthMatchDataExtractor;

@Singleton
final class SiteApiEndpointHandler implements HttpHandler {

    private final SiteApi siteApi;
    private final AccountIdExtractor accountIdExtractor;
    private final AuthMatchDataExtractor authDataExtractor;

    @Inject
    public SiteApiEndpointHandler(
            SiteApi siteApi, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        this.siteApi = siteApi;
        this.accountIdExtractor = accountIdExtractor;
        this.authDataExtractor = authDataExtractor;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        RequestParser parser = RequestParser.create(exchange);
        switch (exchange.getRelativePath()) {
            case "/verification-state" -> handleVerificationState(exchange);
            case "/verification-session" -> handleVerificationSession(exchange);
            case "/age-certificate" -> parser.readBody(new TypeReference<>() {}, this::handleAgeCertificate);
            default -> UndertowStatusCodeSender.create(exchange).sendErrorCode(StatusCodes.NOT_FOUND);
        }
    }

    private void handleVerificationState(HttpServerExchange exchange) throws Exception {
        ValueSender<VerificationState> sender = UndertowJsonValueSender.create(exchange);

        HttpOptional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        if (maybeAccountId.isEmpty()) {
            sender.sendErrorCode(maybeAccountId.statusCode());
            return;
        }
        String accountId = maybeAccountId.get();

        Dispatcher dispatcher = UndertowDispatcher.create(exchange);
        siteApi.getVerificationState(sender, accountId, dispatcher);
    }

    private void handleVerificationSession(HttpServerExchange exchange) throws Exception {
        ValueSender<VerificationSession> sender = UndertowJsonValueSender.create(exchange);

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

        Dispatcher dispatcher = UndertowDispatcher.create(exchange);
        siteApi.createVerificationSession(sender, accountId, authData, dispatcher);
    }

    private void handleAgeCertificate(
            HttpServerExchange exchange, RequestParser parser, SignedAgeCertificate signedCertificate)
            throws Exception {
        StatusCodeSender sender = UndertowStatusCodeSender.create(exchange);

        Dispatcher dispatcher = UndertowDispatcher.create(exchange);
        siteApi.processAgeCertificate(sender, signedCertificate, dispatcher);
    }
}
