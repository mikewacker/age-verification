package org.example.age.api.avs.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.avs.AvsApi;
import org.example.age.api.common.VerificationState;
import org.example.age.api.infra.UndertowJsonApiHandler;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.module.extractor.common.AccountIdExtractor;
import org.example.age.module.extractor.common.AuthMatchDataExtractor;

@Singleton
final class AvsApiEndpointHandler implements HttpHandler {

    private final HttpHandler verificationStateHandler;
    private final HttpHandler verificationSessionHandler;
    private final HttpHandler linkedVerificationRequestHandler;
    private final HttpHandler ageCertificateHandler;
    private final HttpHandler notFoundHandler;

    @Inject
    public AvsApiEndpointHandler(
            AvsApi avsApi, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        verificationStateHandler = UndertowJsonApiHandler.builder(new TypeReference<VerificationState>() {})
                .addExtractor(accountIdExtractor)
                .build(avsApi::getVerificationState);
        verificationSessionHandler = UndertowJsonApiHandler.builder(new TypeReference<VerificationSession>() {})
                .addQueryParam("site-id")
                .build(avsApi::createVerificationSession);
        linkedVerificationRequestHandler = UndertowJsonApiHandler.builder()
                .addExtractor(accountIdExtractor)
                .addQueryParam("request-id", new TypeReference<SecureId>() {})
                .build(avsApi::linkVerificationRequest);
        ageCertificateHandler = UndertowJsonApiHandler.builder()
                .addExtractor(accountIdExtractor)
                .addExtractor(authDataExtractor)
                .build(avsApi::sendAgeCertificate);
        notFoundHandler = UndertowJsonApiHandler.notFound();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        switch (exchange.getRelativePath()) {
            case "/verification-state" -> verificationStateHandler.handleRequest(exchange);
            case "/verification-session" -> verificationSessionHandler.handleRequest(exchange);
            case "/linked-verification-request" -> linkedVerificationRequestHandler.handleRequest(exchange);
            case "/age-certificate" -> ageCertificateHandler.handleRequest(exchange);
            default -> notFoundHandler.handleRequest(exchange);
        }
    }
}
