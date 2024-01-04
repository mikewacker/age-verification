package org.example.age.api.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.endpoint.HttpMethod;
import io.github.mikewacker.drift.endpoint.UndertowArgs;
import io.github.mikewacker.drift.endpoint.UndertowJsonApiHandler;
import io.github.mikewacker.drift.endpoint.UndertowRouter;
import io.undertow.server.HttpHandler;
import org.example.age.api.def.AvsApi;
import org.example.age.api.def.VerificationState;
import org.example.age.api.extractor.AccountIdExtractor;
import org.example.age.api.extractor.AuthMatchDataExtractor;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;

/** Endpoint for an {@link AvsApi}. */
final class AvsApiEndpoint {

    /** Creates an {@link HttpHandler} from an {@link AvsApi} (and some extractors). */
    public static HttpHandler createHandler(
            AvsApi api, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        HttpHandler verificationStateHandler = UndertowJsonApiHandler.builder(new TypeReference<VerificationState>() {})
                .addArg(accountIdExtractor)
                .build(api::getVerificationState);
        HttpHandler verificationSessionHandler = UndertowJsonApiHandler.builder(
                        new TypeReference<VerificationSession>() {})
                .addArg(UndertowArgs.queryParam("site-id"))
                .build(api::createVerificationSession);
        HttpHandler linkedVerificationRequestHandler = UndertowJsonApiHandler.builder()
                .addArg(accountIdExtractor)
                .addArg(UndertowArgs.queryParam("request-id", new TypeReference<SecureId>() {}))
                .build(api::linkVerificationRequest);
        HttpHandler ageCertificateHandler = UndertowJsonApiHandler.builder(new TypeReference<String>() {})
                .addArg(accountIdExtractor)
                .addArg(authDataExtractor)
                .build(api::sendAgeCertificate);

        return UndertowRouter.builder()
                .addRoute(HttpMethod.GET, "/verification-state", verificationStateHandler)
                .addRoute(HttpMethod.POST, "/verification-session", verificationSessionHandler)
                .addRoute(HttpMethod.POST, "/linked-verification-request", linkedVerificationRequestHandler)
                .addRoute(HttpMethod.POST, "/age-certificate", ageCertificateHandler)
                .build();
    }

    // static class
    private AvsApiEndpoint() {}
}
