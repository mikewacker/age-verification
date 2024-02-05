package org.example.age.api.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.endpoint.HttpMethod;
import io.github.mikewacker.drift.endpoint.UndertowArgs;
import io.github.mikewacker.drift.endpoint.UndertowJsonApiHandler;
import io.github.mikewacker.drift.endpoint.UndertowJsonApiRouter;
import io.undertow.server.HttpHandler;
import org.example.age.api.def.SiteApi;
import org.example.age.api.def.VerificationState;
import org.example.age.api.extractor.AccountIdExtractor;
import org.example.age.api.extractor.AuthMatchDataExtractor;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;

/** Endpoint for a {@link SiteApi}. */
final class SiteApiEndpoint {

    /** Creates an {@link HttpHandler} from a {@link SiteApi} (and some extractors). */
    public static HttpHandler createHandler(
            SiteApi api, AccountIdExtractor accountIdExtractor, AuthMatchDataExtractor authDataExtractor) {
        return UndertowJsonApiRouter.of(
                UndertowJsonApiHandler.builder()
                        .route(HttpMethod.GET, "/verification-state")
                        .jsonResponse(new TypeReference<VerificationState>() {})
                        .arg(accountIdExtractor)
                        .apiHandler(api::getVerificationState)
                        .build(),
                UndertowJsonApiHandler.builder()
                        .route(HttpMethod.POST, "/verification-request/create")
                        .jsonResponse(new TypeReference<VerificationRequest>() {})
                        .arg(accountIdExtractor)
                        .arg(authDataExtractor)
                        .apiHandler(api::createVerificationRequest)
                        .build(),
                UndertowJsonApiHandler.builder()
                        .route(HttpMethod.POST, "/age-certificate/process")
                        .jsonResponse(new TypeReference<String>() {})
                        .arg(UndertowArgs.body(new TypeReference<SignedAgeCertificate>() {}))
                        .apiHandler(api::processAgeCertificate)
                        .build());
    }

    // static class
    private SiteApiEndpoint() {}
}
