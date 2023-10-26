package org.example.age.common.site.api;

import com.google.common.net.HostAndPort;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.security.PublicKey;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.example.age.common.account.AccountIdExtractor;
import org.example.age.common.client.internal.RequestDispatcher;
import org.example.age.common.site.auth.internal.AuthManager;
import org.example.age.common.site.verification.internal.VerificationManager;
import org.example.age.common.utils.internal.ExchangeUtils;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationSession;

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
    private final Supplier<HostAndPort> avsHostAndPortSupplier;
    private final Supplier<PublicKey> avsPublicSigningKeySupplier;
    private final Supplier<String> siteIdSupplier;

    @Inject
    public SiteApiHandler(
            AccountIdExtractor accountIdExtractor,
            AuthManager authManager,
            VerificationManager verificationManager,
            RequestDispatcher requestDispatcher,
            @Named("avs") Supplier<HostAndPort> avsHostAndPortSupplier,
            @Named("avsSigning") Supplier<PublicKey> avsPublicSigningKeySupplier,
            @Named("siteId") Supplier<String> siteIdSupplier) {
        this.accountIdExtractor = accountIdExtractor;
        this.authManager = authManager;
        this.verificationManager = verificationManager;
        this.requestDispatcher = requestDispatcher;
        this.avsHostAndPortSupplier = avsHostAndPortSupplier;
        this.avsPublicSigningKeySupplier = avsPublicSigningKeySupplier;
        this.siteIdSupplier = siteIdSupplier;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        switch (exchange.getRelativePath()) {
            case "/verification-session" -> handleVerificationSessionRequest(exchange);
            case "/age-certificate" -> ExchangeUtils.handleRequestWithBody(
                    exchange, this::verifyAgeCertificate, this::handleAgeCertificateRequest);
            default -> ExchangeUtils.sendStatusCode(exchange, StatusCodes.NOT_FOUND);
        }
    }

    /** Handles a request to create a {@link VerificationSession} for an account. */
    private void handleVerificationSessionRequest(HttpServerExchange exchange) {
        Optional<String> maybeAccountId = accountIdExtractor.tryExtract(exchange);
        if (maybeAccountId.isEmpty()) {
            ExchangeUtils.sendStatusCode(exchange, StatusCodes.UNAUTHORIZED);
            return;
        }

        String accountId = maybeAccountId.get();
        Request request = createVerificationSessionRequest();
        requestDispatcher.dispatchWithResponseBody(
                request,
                exchange,
                VerificationSession::deserialize,
                (response, session, ex) -> onVerificationSessionResponseReceived(accountId, response, session, ex));
    }

    /** Creates a backend request to obtain a {@link VerificationSession}. */
    private Request createVerificationSessionRequest() {
        HostAndPort avsHostAndPort = avsHostAndPortSupplier.get();
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(avsHostAndPort.getHost())
                .port(avsHostAndPort.getPort())
                .addPathSegments("api/verification-session")
                .addQueryParameter("site-id", siteIdSupplier.get())
                .build();
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
        ExchangeUtils.sendResponseBody(exchange, "application/json", session, VerificationSession::serialize);
    }

    /** Verifies a signed {@link AgeCertificate}. */
    private AgeCertificate verifyAgeCertificate(byte[] signedCertificate) {
        return AgeCertificate.verifyForSite(signedCertificate, avsPublicSigningKeySupplier.get(), siteIdSupplier.get());
    }

    /** Handles a request to process an {@link AgeCertificate}. */
    private void handleAgeCertificateRequest(HttpServerExchange exchange, AgeCertificate certificate) {
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
}
