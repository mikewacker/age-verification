package org.example.age.api.endpoint.avs;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import org.example.age.api.def.avs.AvsApi;
import org.example.age.api.def.common.VerificationState;
import org.example.age.api.extractor.common.AccountIdExtractor;
import org.example.age.api.extractor.common.AuthMatchDataExtractor;
import org.example.age.api.infra.UndertowApiRouter;
import org.example.age.api.infra.UndertowJsonApiHandler;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Endpoint for an {@link AvsApi}. */
public final class AvsApiEndpoint {

    /** Creates an {@link HttpHandler} from an {@link AvsApi} (and some extractors). */
    public static HttpHandler createHandler(
            AvsApi api, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        HttpHandler verificationStateHandler = UndertowJsonApiHandler.builder(new TypeReference<VerificationState>() {})
                .addExtractor(accountIdExtractor)
                .build(api::getVerificationState);
        HttpHandler verificationSessionHandler = UndertowJsonApiHandler.builder(
                        new TypeReference<VerificationSession>() {})
                .addQueryParam("site-id")
                .build(api::createVerificationSession);
        HttpHandler linkedVerificationRequestHandler = UndertowJsonApiHandler.builder()
                .addExtractor(accountIdExtractor)
                .addQueryParam("request-id", new TypeReference<SecureId>() {})
                .build(api::linkVerificationRequest);
        HttpHandler ageCertificateHandler = UndertowJsonApiHandler.builder()
                .addExtractor(accountIdExtractor)
                .addExtractor(authDataExtractor)
                .build(api::sendAgeCertificate);

        return UndertowApiRouter.builder()
                .addRoute("/verification-state", verificationStateHandler)
                .addRoute("/verification-session", verificationSessionHandler)
                .addRoute("/linked-verification-request", linkedVerificationRequestHandler)
                .addRoute("/age-certificate", ageCertificateHandler)
                .build();
    }

    // static class
    private AvsApiEndpoint() {}
}
