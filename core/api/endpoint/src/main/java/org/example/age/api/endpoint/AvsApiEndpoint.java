package org.example.age.api.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.endpoint.HttpMethod;
import io.github.mikewacker.drift.endpoint.UndertowArgs;
import io.github.mikewacker.drift.endpoint.UndertowJsonApiHandler;
import io.github.mikewacker.drift.endpoint.UndertowJsonApiRouter;
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
        return UndertowJsonApiRouter.of(
                UndertowJsonApiHandler.builder()
                        .route(HttpMethod.GET, "/verification-state")
                        .jsonResponse(new TypeReference<VerificationState>() {})
                        .arg(accountIdExtractor)
                        .apiHandler(api::getVerificationState)
                        .build(),
                UndertowJsonApiHandler.builder()
                        .route(HttpMethod.POST, "/verification-session/create")
                        .jsonResponse(new TypeReference<VerificationSession>() {})
                        .arg(UndertowArgs.queryParam("site-id"))
                        .apiHandler(api::createVerificationSession)
                        .build(),
                UndertowJsonApiHandler.builder()
                        .route(HttpMethod.POST, "/verification-request/link")
                        .statusCodeResponse()
                        .arg(accountIdExtractor)
                        .arg(UndertowArgs.queryParam("request-id", new TypeReference<SecureId>() {}))
                        .apiHandler(api::linkVerificationRequest)
                        .build(),
                UndertowJsonApiHandler.builder()
                        .route(HttpMethod.POST, "/age-certificate/send")
                        .jsonResponse(new TypeReference<String>() {})
                        .arg(accountIdExtractor)
                        .arg(authDataExtractor)
                        .apiHandler(api::sendAgeCertificate)
                        .build());
    }

    // static class
    private AvsApiEndpoint() {}
}
