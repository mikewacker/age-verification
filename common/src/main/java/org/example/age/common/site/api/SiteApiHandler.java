package org.example.age.common.site.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import java.security.PublicKey;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AccountIdExtractor;
import org.example.age.common.base.client.internal.RequestDispatcher;
import org.example.age.common.site.auth.internal.AuthManager;
import org.example.age.common.site.verification.internal.VerificationManager;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.utils.DataMapper;
import org.example.age.infra.api.exchange.ExchangeUtils;
import org.example.age.site.api.AvsLocation;

/**
 * HTTP handler for a site's age verification API.
 *
 * <p>In a real implementation, the backend call to the AVS would be authenticated and use HTTPS.</p>
 */
@Singleton
final class SiteApiHandler implements HttpHandler {

    private static final RequestBody EMPTY_BODY = RequestBody.create(new byte[0]);

    private final AccountIdExtractor accountIdExtractor;
    private final AuthManager authManager;
    private final VerificationManager verificationManager;
    private final RequestDispatcher requestDispatcher;
    private final Provider<AvsLocation> avsLocationProvider;
    private final Provider<PublicKey> avsPublicSigningKeyProvider;
    private final Provider<String> siteIdProvider;

    @Inject
    public SiteApiHandler(
            AccountIdExtractor accountIdExtractor,
            AuthManager authManager,
            VerificationManager verificationManager,
            RequestDispatcher requestDispatcher,
            Provider<AvsLocation> avsLocationProvider,
            @Named("avsSigning") Provider<PublicKey> avsPublicSigningKeyProvider,
            @Named("siteId") Provider<String> siteIdProvider) {
        this.accountIdExtractor = accountIdExtractor;
        this.authManager = authManager;
        this.verificationManager = verificationManager;
        this.requestDispatcher = requestDispatcher;
        this.avsLocationProvider = avsLocationProvider;
        this.avsPublicSigningKeyProvider = avsPublicSigningKeyProvider;
        this.siteIdProvider = siteIdProvider;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        switch (exchange.getRelativePath()) {
            case "/verification-session" -> handleVerificationSessionRequest(exchange);
            case "/age-certificate" -> ExchangeUtils.handleRequestWithBody(
                    exchange, SiteApiHandler::deserializeSignedAgeCertificate, this::handleSignedAgeCertificateRequest);
            default -> ExchangeUtils.sendStatusCode(exchange, StatusCodes.NOT_FOUND);
        }
    }

    /** Handles a request to create a {@link VerificationSession} for an account. */
    private void handleVerificationSessionRequest(HttpServerExchange exchange) {
        HttpOptional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        if (maybeAccountId.isEmpty()) {
            ExchangeUtils.sendStatusCode(exchange, maybeAccountId.statusCode());
            return;
        }

        String accountId = maybeAccountId.get();
        Request request = createVerificationSessionRequest();
        requestDispatcher.dispatchWithResponseBody(
                request,
                exchange,
                SiteApiHandler::deserializeVerificationSession,
                (response, session, ex) -> onVerificationSessionResponseReceived(accountId, response, session, ex));
    }

    /** Creates a backend request to obtain a {@link VerificationSession}. */
    private Request createVerificationSessionRequest() {
        HttpUrl url = avsLocationProvider.get().verificationSessionUrl(siteIdProvider.get());
        return new Request.Builder().url(url).post(EMPTY_BODY).build();
    }

    /** Called when a response is received to the request to create a {@link VerificationSession}. */
    private void onVerificationSessionResponseReceived(
            String accountId, Response response, VerificationSession session, HttpServerExchange exchange) {
        if (!response.isSuccessful()) {
            boolean is5xxError = (response.code() / 100) == 5;
            int statusCode = is5xxError ? StatusCodes.BAD_GATEWAY : StatusCodes.INTERNAL_SERVER_ERROR;
            ExchangeUtils.sendStatusCode(exchange, statusCode);
            return;
        }

        authManager.onVerificationSessionReceived(session, exchange);
        verificationManager.onVerificationSessionReceived(accountId, session, exchange);
        ExchangeUtils.sendResponseBody(exchange, "application/json", session, SiteApiHandler::serialize);
    }

    /** Processes a {@link SignedAgeCertificate}. */
    private void handleSignedAgeCertificateRequest(
            HttpServerExchange exchange, SignedAgeCertificate signedCertificate) {
        if (!verifySignedAgeCertificate(exchange, signedCertificate)) {
            return;
        }
        AgeCertificate certificate = signedCertificate.ageCertificate();

        int authStatusCode = authManager.onAgeCertificateReceived(certificate);
        if (authStatusCode != StatusCodes.OK) {
            ExchangeUtils.sendStatusCode(exchange, authStatusCode);
            return;
        }

        int verifyStatusCode = verificationManager.onAgeCertificateReceived(certificate);
        if (verifyStatusCode != StatusCodes.OK) {
            ExchangeUtils.sendStatusCode(exchange, verifyStatusCode);
            return;
        }

        ExchangeUtils.sendStatusCode(exchange, StatusCodes.OK);
    }

    /** Verifies a {@link SignedAgeCertificate}, sending an error code if verification fails. */
    private boolean verifySignedAgeCertificate(HttpServerExchange exchange, SignedAgeCertificate signedCertificate) {
        if (!signedCertificate.verify(avsPublicSigningKeyProvider.get())) {
            ExchangeUtils.sendStatusCode(exchange, StatusCodes.UNAUTHORIZED);
            return false;
        }

        AgeCertificate certificate = signedCertificate.ageCertificate();
        VerificationRequest request = certificate.verificationRequest();
        if (!request.isIntendedRecipient(siteIdProvider.get())) {
            ExchangeUtils.sendStatusCode(exchange, StatusCodes.FORBIDDEN);
            return false;
        }

        if (request.isExpired()) {
            ExchangeUtils.sendStatusCode(exchange, StatusCodes.GONE);
            return false;
        }

        return true;
    }

    private static byte[] serialize(Object value) {
        try {
            return DataMapper.get().writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("serialization failed", e);
        }
    }

    private static VerificationSession deserializeVerificationSession(byte[] rawSession) {
        return deserialize(rawSession, new TypeReference<>() {});
    }

    private static SignedAgeCertificate deserializeSignedAgeCertificate(byte[] rawSignedCertificate) {
        return deserialize(rawSignedCertificate, new TypeReference<>() {});
    }

    private static <V> V deserialize(byte[] rawOjbect, TypeReference<V> valueTypeRef) {
        try {
            return DataMapper.get().readValue(rawOjbect, valueTypeRef);
        } catch (IOException e) {
            throw new RuntimeException("deserialization failed", e);
        }
    }
}
