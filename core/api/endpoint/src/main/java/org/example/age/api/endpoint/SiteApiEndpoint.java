package org.example.age.api.endpoint;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.endpoint.HttpMethod;
import io.github.mikewacker.drift.endpoint.UndertowArgs;
import io.github.mikewacker.drift.endpoint.UndertowJsonApiHandler;
import io.github.mikewacker.drift.endpoint.UndertowRouter;
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
        HttpHandler verificationStateHandler = UndertowJsonApiHandler.builder(new TypeReference<VerificationState>() {})
                .addArg(accountIdExtractor)
                .build(api::getVerificationState);
        HttpHandler verificationRequestHandler = UndertowJsonApiHandler.builder(
                        new TypeReference<VerificationRequest>() {})
                .addArg(accountIdExtractor)
                .addArg(authDataExtractor)
                .build(api::createVerificationRequest);
        HttpHandler ageCertificateHandler = UndertowJsonApiHandler.builder(new TypeReference<String>() {})
                .addArg(UndertowArgs.body(new TypeReference<SignedAgeCertificate>() {}))
                .build(api::processAgeCertificate);

        return UndertowRouter.builder()
                .addRoute(HttpMethod.GET, "/verification-state", verificationStateHandler)
                .addRoute(HttpMethod.POST, "/verification-request", verificationRequestHandler)
                .addRoute(HttpMethod.POST, "/age-certificate", ageCertificateHandler)
                .build();
    }

    // static class
    private SiteApiEndpoint() {}
}
