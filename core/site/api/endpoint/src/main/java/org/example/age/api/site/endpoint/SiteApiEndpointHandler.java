package org.example.age.api.site.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.common.VerificationState;
import org.example.age.api.infra.UndertowJsonApiHandler;
import org.example.age.api.site.SiteApi;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.module.extractor.common.AccountIdExtractor;
import org.example.age.module.extractor.common.AuthMatchDataExtractor;

@Singleton
final class SiteApiEndpointHandler implements HttpHandler {

    private final HttpHandler verificationStateHandler;
    private final HttpHandler verificationSessionHandler;
    private final HttpHandler ageCertificateHandler;
    private final HttpHandler notFoundHandler;

    @Inject
    public SiteApiEndpointHandler(
            SiteApi siteApi, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        verificationStateHandler = UndertowJsonApiHandler.builder(new TypeReference<VerificationState>() {})
                .addExtractor(accountIdExtractor)
                .build(siteApi::getVerificationState);
        verificationSessionHandler = UndertowJsonApiHandler.builder(new TypeReference<VerificationSession>() {})
                .addExtractor(accountIdExtractor)
                .addExtractor(authDataExtractor)
                .build(siteApi::createVerificationSession);
        ageCertificateHandler = UndertowJsonApiHandler.builder()
                .addBody(new TypeReference<SignedAgeCertificate>() {})
                .build(siteApi::processAgeCertificate);
        notFoundHandler = UndertowJsonApiHandler.notFound();
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        switch (exchange.getRelativePath()) {
            case "/verification-state" -> verificationStateHandler.handleRequest(exchange);
            case "/verification-session" -> verificationSessionHandler.handleRequest(exchange);
            case "/age-certificate" -> ageCertificateHandler.handleRequest(exchange);
            default -> notFoundHandler.handleRequest(exchange);
        }
    }
}
