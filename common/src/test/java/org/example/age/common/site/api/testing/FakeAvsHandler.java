package org.example.age.common.site.api.testing;

import com.google.common.net.HostAndPort;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import java.security.PrivateKey;
import java.time.Duration;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.example.age.common.api.data.auth.AuthMatchDataExtractor;
import org.example.age.common.api.exchange.impl.ExchangeUtils;
import org.example.age.common.base.client.internal.RequestDispatcher;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.AuthToken;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;

/**
 * Fake age verification service that can create a {@link VerificationSession} for a site
 * and an {@link AgeCertificate} for a pseudonym.
 */
@Singleton
public final class FakeAvsHandler implements HttpHandler {

    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);

    private final AuthMatchDataExtractor authDataExtractor;
    private final RequestDispatcher requestDispatcher;
    private final Provider<HostAndPort> siteHostAndPortProvider;
    private final Provider<PrivateKey> privateSigningKeyProvider;

    private VerificationSession session = null;

    @Inject
    public FakeAvsHandler(
            AuthMatchDataExtractor authDataExtractor,
            RequestDispatcher requestDispatcher,
            @Named("site") Provider<HostAndPort> siteHostAndPortProvider,
            @Named("signing") Provider<PrivateKey> privateSigningKeyProvider) {
        this.authDataExtractor = authDataExtractor;
        this.requestDispatcher = requestDispatcher;
        this.siteHostAndPortProvider = siteHostAndPortProvider;
        this.privateSigningKeyProvider = privateSigningKeyProvider;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        switch (exchange.getRequestPath()) {
            case "/api/verification-session" -> handleVerificationSessionRequest(exchange);
            case "/api/age-certificate" -> handleAgeCertificateRequest(exchange);
            default -> ExchangeUtils.sendStatusCode(exchange, 418);
        }
    }

    /** Handles a request to create a {@link VerificationSession} with the provided site ID. */
    private void handleVerificationSessionRequest(HttpServerExchange exchange) {
        Optional<String> maybeSiteId = ExchangeUtils.tryGetQueryParameter(exchange, "site-id");
        if (maybeSiteId.isEmpty()) {
            ExchangeUtils.sendStatusCode(exchange, 418);
            return;
        }

        String siteId = maybeSiteId.get();
        session = createVerificationSession(siteId);
        ExchangeUtils.sendResponseBody(exchange, "application/json", session, VerificationSession::serialize);
    }

    /** Handles a request to send an {@link AgeCertificate} for the provided pseudonym. */
    private void handleAgeCertificateRequest(HttpServerExchange exchange) {
        if (session == null) {
            ExchangeUtils.sendStatusCode(exchange, 418);
            return;
        }

        Optional<SecureId> maybePseudonym =
                ExchangeUtils.tryGetQueryParameter(exchange, "pseudonym").map(SecureId::fromString);
        if (maybePseudonym.isEmpty()) {
            ExchangeUtils.sendStatusCode(exchange, 418);
            return;
        }

        SecureId pseudonym = maybePseudonym.get();
        AgeCertificate certificate = createAgeCertificate(pseudonym, exchange);
        byte[] signedCertificate = certificate.sign(privateSigningKeyProvider.get());
        session = null;
        Request request = createAgeCertificateRequest(signedCertificate);
        requestDispatcher.dispatchWithoutResponseBody(
                request, exchange, (response, responseBody, ex) -> ExchangeUtils.sendStatusCode(ex, response.code()));
    }

    /** Create a {@link VerificationSession} for the site. */
    private static VerificationSession createVerificationSession(String siteId) {
        VerificationRequest request = VerificationRequest.generateForSite(siteId, EXPIRES_IN);
        return VerificationSession.create(request);
    }

    /** Creates a signed {@link AgeCertificate} for the pseudonym, using the created {@link VerificationSession}. */
    private AgeCertificate createAgeCertificate(SecureId pseudonym, HttpServerExchange exchange) {
        VerificationRequest request = session.verificationRequest();
        VerifiedUser user = VerifiedUser.of(pseudonym, 18);
        AuthToken authToken =
                authDataExtractor.tryExtract(exchange, code -> {}).get().encrypt(session.authKey());
        return AgeCertificate.of(request, user, authToken);
    }

    /** Creates a request to transmit the signed age certificate. */
    private Request createAgeCertificateRequest(byte[] signedCertificate) {
        HostAndPort siteHostAndPort = siteHostAndPortProvider.get();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(siteHostAndPort.getHost())
                .port(siteHostAndPort.getPort())
                .addPathSegments("api/age-certificate")
                .build();
        return new Request.Builder()
                .url(url)
                .post(RequestBody.create(signedCertificate))
                .build();
    }
}
