package org.example.age.common.site.auth.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import dagger.Component;
import dagger.Module;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import java.time.Duration;
import java.util.Map;
import javax.inject.Singleton;
import org.example.age.common.auth.AuthMatchData;
import org.example.age.common.auth.AuthMatchDataExtractor;
import org.example.age.common.auth.UserAgentAuthMatchDataExtractorModule;
import org.example.age.common.store.InMemoryPendingStoreFactoryModule;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.testing.TestExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class AuthManagerTest {

    private AuthManager authManager;
    private AuthMatchDataExtractor authDataExtractor;

    private static AuthKey authKey;
    private static AuthKey otherAuthKey;

    @BeforeEach
    public void createAuthManagerEtAl() {
        TestComponent component = TestComponent.create();
        authManager = component.authManager();
        authDataExtractor = component.authMatchDataExtractor();
    }

    @BeforeAll
    public static void generateKeys() {
        authKey = AuthKey.generate();
        otherAuthKey = AuthKey.generate();
    }

    @Test
    public void authPasses() {
        HttpServerExchange localExchange = createStubExchange("user agent");
        VerificationSession session = createSession();
        authManager.onVerificationSessionReceived(session, localExchange);

        HttpServerExchange remoteExchange = createStubExchange("user agent");
        AgeCertificate certificate = createCertification(session, remoteExchange);
        int statusCode = authManager.onAgeCertificateReceived(certificate);
        assertThat(statusCode).isEqualTo(StatusCodes.OK);
    }

    @Test
    public void authFails_Mismatch() {
        HttpServerExchange localExchange = createStubExchange("user agent 1");
        VerificationSession session = createSession();
        authManager.onVerificationSessionReceived(session, localExchange);

        HttpServerExchange remoteExchange = createStubExchange("user agent 2");
        AgeCertificate certificate = createCertification(session, remoteExchange);
        int statusCode = authManager.onAgeCertificateReceived(certificate);
        assertThat(statusCode).isEqualTo(StatusCodes.UNAUTHORIZED);
    }

    @Test
    public void authFails_NotFound() {
        HttpServerExchange remoteExchange = createStubExchange("user agent");
        VerificationSession session = createSession();
        AgeCertificate certificate = createCertification(session, remoteExchange);
        int statusCode = authManager.onAgeCertificateReceived(certificate);
        assertThat(statusCode).isEqualTo(StatusCodes.NOT_FOUND);
    }

    @Test
    public void authFails_BadAuthToken() {
        HttpServerExchange localExchange = createStubExchange("user agent");
        VerificationSession session = createSession(otherAuthKey);
        authManager.onVerificationSessionReceived(session, localExchange);

        HttpServerExchange remoteExchange = createStubExchange("user agent");
        AgeCertificate certificate = createCertification(session, remoteExchange);
        int statusCode = authManager.onAgeCertificateReceived(certificate);
        assertThat(statusCode).isEqualTo(StatusCodes.UNAUTHORIZED);
    }

    private static HttpServerExchange createStubExchange(String userAgent) {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addRequestHeaders(exchange, Map.of(Headers.USER_AGENT, userAgent));
        TestExchanges.addStubIoThread(exchange);
        return exchange;
    }

    private static VerificationSession createSession() {
        return createSession(authKey);
    }

    private static VerificationSession createSession(AuthKey authKey) {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        return VerificationSession.of(request, authKey);
    }

    private AgeCertificate createCertification(VerificationSession session, HttpServerExchange exchange) {
        VerificationRequest request = session.verificationRequest();
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        AuthMatchData authData = authDataExtractor.extract(exchange);
        AuthToken authToken = authData.encrypt(authKey);
        return AgeCertificate.of(request, user, authToken);
    }

    /** Dagger module that binds dependencies needed to create an {@link AuthManager}. */
    @Module(
            includes = {
                AuthManagerModule.class,
                UserAgentAuthMatchDataExtractorModule.class,
                InMemoryPendingStoreFactoryModule.class,
            })
    interface TestModule {}

    /** Dagger component that provides an {@link AuthManager}, and also an {@link AuthMatchDataExtractor}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerAuthManagerTest_TestComponent.create();
        }

        AuthManager authManager();

        AuthMatchDataExtractor authMatchDataExtractor();
    }
}
