package org.example.age.api.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import org.example.age.api.def.SiteApi;
import org.example.age.api.def.VerificationState;
import org.example.age.api.extractor.AccountIdExtractor;
import org.example.age.api.extractor.AuthMatchDataExtractor;
import org.example.age.api.infra.UndertowApiRouter;
import org.example.age.api.infra.UndertowJsonApiHandler;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;

/** Endpoint for a {@link SiteApi}. */
final class SiteApiEndpoint {

    /** Creates an {@link HttpHandler} from a {@link SiteApi} (and some extractors). */
    public static HttpHandler createHandler(
            SiteApi api, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        HttpHandler verificationStateHandler = UndertowJsonApiHandler.builder(new TypeReference<VerificationState>() {})
                .addExtractor(accountIdExtractor)
                .build(api::getVerificationState);
        HttpHandler verificationRequestHandler = UndertowJsonApiHandler.builder(
                        new TypeReference<VerificationRequest>() {})
                .addExtractor(accountIdExtractor)
                .addExtractor(authDataExtractor)
                .build(api::createVerificationRequest);
        HttpHandler ageCertificateHandler = UndertowJsonApiHandler.builder(new TypeReference<String>() {})
                .addBody(new TypeReference<SignedAgeCertificate>() {})
                .build(api::processAgeCertificate);

        return UndertowApiRouter.builder()
                .addRoute("/verification-state", verificationStateHandler)
                .addRoute("/verification-request", verificationRequestHandler)
                .addRoute("/age-certificate", ageCertificateHandler)
                .build();
    }

    // static class
    private SiteApiEndpoint() {}
}
