package org.example.age.common.site.api.testing;

import com.google.common.net.HostAndPort;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.example.age.common.client.internal.RequestDispatcher;
import org.example.age.common.site.auth.AuthMatchDataExtractor;
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
    private final Supplier<HostAndPort> siteHostAndPortSupplier;
    private final Supplier<PrivateKey> privateSigningKeySupplier;

    private VerificationSession session = null;

    @Inject
    public FakeAvsHandler(
            AuthMatchDataExtractor authDataExtractor,
            RequestDispatcher requestDispatcher,
            @Named("site") Supplier<HostAndPort> siteHostAndPortSupplier,
            @Named("signing") Supplier<PrivateKey> privateSigningKeySupplier) {
        this.authDataExtractor = authDataExtractor;
        this.requestDispatcher = requestDispatcher;
        this.siteHostAndPortSupplier = siteHostAndPortSupplier;
        this.privateSigningKeySupplier = privateSigningKeySupplier;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        switch (exchange.getRequestPath()) {
            case "/api/verification-session" -> handleVerificationSessionRequest(exchange);
            case "/api/age-certificate" -> handleAgeCertificateRequest(exchange);
            default -> sendStatusCode(exchange, 418);
        }
    }

    /** Handles a request to create a {@link VerificationSession} with the provided site ID. */
    private void handleVerificationSessionRequest(HttpServerExchange exchange) {
        Optional<String> maybeSiteId = tryGetQueryParameter(exchange, "site-id");
        if (maybeSiteId.isEmpty()) {
            sendStatusCode(exchange, 418);
            return;
        }

        String siteId = maybeSiteId.get();
        session = createVerificationSession(siteId);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(ByteBuffer.wrap(session.serialize()));
    }

    /** Handles a request to send an {@link AgeCertificate} for the provided pseudonym. */
    private void handleAgeCertificateRequest(HttpServerExchange exchange) {
        if (session == null) {
            sendStatusCode(exchange, 418);
            return;
        }

        Optional<SecureId> maybePseudonym =
                tryGetQueryParameter(exchange, "pseudonym").map(SecureId::fromString);
        if (maybePseudonym.isEmpty()) {
            sendStatusCode(exchange, 418);
            return;
        }

        SecureId pseudonym = maybePseudonym.get();
        AgeCertificate certificate = createAgeCertificate(pseudonym, exchange);
        byte[] signedCertificate = certificate.sign(privateSigningKeySupplier.get());
        session = null;
        Request request = createAgeCertificateRequest(signedCertificate);
        requestDispatcher.dispatch(
                request, exchange, (response, responseBody, ex) -> sendStatusCode(ex, response.code()));
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
        AuthToken authToken = authDataExtractor.extract(exchange).encrypt(session.authKey());
        return AgeCertificate.of(request, user, authToken);
    }

    /** Creates a request to transmit the signed age certificate. */
    private Request createAgeCertificateRequest(byte[] signedCertificate) {
        HostAndPort siteHostAndPort = siteHostAndPortSupplier.get();
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

    /** Tries to get the value of a query parameter. */
    private Optional<String> tryGetQueryParameter(HttpServerExchange exchange, String name) {
        Deque<String> values = exchange.getQueryParameters().getOrDefault(name, new ArrayDeque<>());
        return !values.isEmpty() ? Optional.of(values.getFirst()) : Optional.empty();
    }

    /** Sends only a status code as the response. */
    private void sendStatusCode(HttpServerExchange exchange, int statusCode) {
        exchange.setStatusCode(statusCode);
        exchange.endExchange();
    }
}
