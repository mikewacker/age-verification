package org.example.age.api.endpoint.site;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import org.example.age.api.def.common.VerificationState;
import org.example.age.api.def.site.SiteApi;
import org.example.age.api.infra.UndertowApiRouter;
import org.example.age.api.infra.UndertowJsonApiHandler;
import org.example.age.api.module.extractor.common.AccountIdExtractor;
import org.example.age.api.module.extractor.common.AuthMatchDataExtractor;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;

/** Endpoint for a {@link SiteApi}. */
public final class SiteApiEndpoint {

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
        HttpHandler ageCertificateHandler = UndertowJsonApiHandler.builder()
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