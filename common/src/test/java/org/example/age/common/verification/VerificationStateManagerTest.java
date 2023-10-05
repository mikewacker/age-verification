package org.example.age.common.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import dagger.Component;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.StatusCodes;
import java.time.Duration;
import java.util.Map;
import javax.inject.Singleton;
import org.assertj.core.data.Offset;
import org.example.age.certificate.AgeCertificate;
import org.example.age.certificate.AuthKey;
import org.example.age.certificate.AuthToken;
import org.example.age.certificate.VerificationRequest;
import org.example.age.certificate.VerificationSession;
import org.example.age.common.verification.auth.AuthMatchData;
import org.example.age.common.verification.auth.AuthMatchDataExtractor;
import org.example.age.common.verification.auth.UserAgentAuthMatchModule;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.testing.TestExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class VerificationStateManagerTest {

    private static AuthKey authKey;
    private static AuthKey otherAuthKey;

    private VerificationStateManager stateManager;
    private AuthMatchDataExtractor authDataExtractor;

    @BeforeAll
    public static void generateKeys() {
        authKey = AuthKey.generate();
        otherAuthKey = AuthKey.generate();
    }

    @BeforeEach
    public void createStateManagerAndAuthDataExtractor() {
        TestComponent component = TestComponent.create();
        stateManager = component.verificationStateManager();
        authDataExtractor = component.authMatchDataExtractor();
    }

    @Test
    public void verify() {
        HttpServerExchange exchange = createStubExchange("username");
        VerificationState initialState = stateManager.getVerificationState(exchange);
        assertThat(initialState.status()).isEqualTo(VerificationStatus.UNVERIFIED);

        VerificationSession session = createSession();
        int statusCode1 = stateManager.onVerificationSessionReceived(session, exchange);
        assertThat(statusCode1).isEqualTo(StatusCodes.OK);

        VerifiedUser user = createUser();
        AgeCertificate certificate = createCertification(session, user, exchange);
        int statusCode2 = stateManager.onAgeCertificateReceived(certificate);
        long now = System.currentTimeMillis() / 1000;
        long expectedExpiration = now + 3600;
        assertThat(statusCode2).isEqualTo(StatusCodes.OK);
        VerificationState state = stateManager.getVerificationState(exchange);
        assertThat(state.status()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.verifiedUser()).isEqualTo(user);
        assertThat(state.expiration()).isCloseTo(expectedExpiration, Offset.offset(1L));
    }

    @Test
    public void reVerify() {
        HttpServerExchange exchange = createStubExchange("username");
        VerificationSession session1 = createSession();
        stateManager.onVerificationSessionReceived(session1, exchange);

        VerifiedUser user = createUser();
        AgeCertificate certificate1 = createCertification(session1, user, exchange);
        int statusCode1 = stateManager.onAgeCertificateReceived(certificate1);
        assertThat(statusCode1).isEqualTo(StatusCodes.OK);

        VerificationSession session2 = createSession();
        stateManager.onVerificationSessionReceived(session2, exchange);

        AgeCertificate certificate2 = createCertification(session2, user, exchange);
        int statusCode2 = stateManager.onAgeCertificateReceived(certificate2);
        assertThat(statusCode2).isEqualTo(StatusCodes.OK);
    }

    @Test
    public void getStateWithoutAccount() {
        HttpServerExchange exchange = createStubExchange("");
        VerificationState initialState = stateManager.getVerificationState(exchange);
        assertThat(initialState.status()).isEqualTo(VerificationStatus.UNVERIFIED);
    }

    @Test
    public void failToVerify_AuthenticationFails() {
        HttpServerExchange exchange1 = createStubExchange("username", "user agent 1");
        VerificationSession session = createSession();
        stateManager.onVerificationSessionReceived(session, exchange1);

        HttpServerExchange exchange2 = createStubExchange("username", "user agent 2");
        VerifiedUser user = createUser();
        AgeCertificate certificate = createCertification(session, user, exchange2);
        int statusCode = stateManager.onAgeCertificateReceived(certificate);
        assertThat(statusCode).isEqualTo(StatusCodes.FORBIDDEN);
    }

    @Test
    public void failToVerify_DuplicateVerification() {
        HttpServerExchange exchange1 = createStubExchange("username1");
        VerificationSession session1 = createSession();
        stateManager.onVerificationSessionReceived(session1, exchange1);

        VerifiedUser user = createUser();
        AgeCertificate certificate1 = createCertification(session1, user, exchange1);
        int statusCode1 = stateManager.onAgeCertificateReceived(certificate1);
        assertThat(statusCode1).isEqualTo(StatusCodes.OK);

        HttpServerExchange exchange2 = createStubExchange("username2");
        VerificationSession session2 = createSession();
        stateManager.onVerificationSessionReceived(session2, exchange2);

        AgeCertificate certificate2 = createCertification(session2, user, exchange2);
        int statusCode2 = stateManager.onAgeCertificateReceived(certificate2);
        assertThat(statusCode2).isEqualTo(StatusCodes.CONFLICT);
    }

    @Test
    public void failToVerify_AccountNotFound() {
        HttpServerExchange exchange = createStubExchange("");
        VerificationSession session = createSession();
        int statusCode = stateManager.onVerificationSessionReceived(session, exchange);
        assertThat(statusCode).isEqualTo(StatusCodes.NOT_FOUND);
    }

    @Test
    public void failToVerify_PendingVerificationNotFound() {
        HttpServerExchange exchange = createStubExchange("username");
        VerificationSession session = createSession();
        VerifiedUser user = createUser();
        AgeCertificate certificate = createCertification(session, user, exchange);
        int statusCode = stateManager.onAgeCertificateReceived(certificate);
        assertThat(statusCode).isEqualTo(StatusCodes.NOT_FOUND);
    }

    @Test
    public void failToVerify_AuthenticationError() {
        HttpServerExchange exchange = createStubExchange("username");
        VerificationSession session = createSession(otherAuthKey);
        stateManager.onVerificationSessionReceived(session, exchange);

        VerifiedUser user = createUser();
        AgeCertificate certificate = createCertification(session, user, exchange);
        int statusCode = stateManager.onAgeCertificateReceived(certificate);
        assertThat(statusCode).isEqualTo(StatusCodes.FORBIDDEN);
    }

    private static HttpServerExchange createStubExchange(String accountId) {
        return createStubExchange(accountId, "user agent");
    }

    private static HttpServerExchange createStubExchange(String accountId, String userAgent) {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addRequestHeaders(
                exchange, Map.of(Headers.USER_AGENT, userAgent, new HttpString("Account-Id"), accountId));
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

    private static VerifiedUser createUser() {
        return VerifiedUser.of(SecureId.generate(), 18);
    }

    private AgeCertificate createCertification(
            VerificationSession session, VerifiedUser user, HttpServerExchange exchange) {
        AuthMatchData authData = authDataExtractor.extract(exchange);
        AuthToken authToken = authData.encrypt(authKey);
        return AgeCertificate.of(session.verificationRequest(), user, authToken);
    }

    /** Dagger component that provides a {@link VerificationStateManager} and an {@link AuthMatchDataExtractor}. */
    @Component(modules = {VerificationModule.class, TestUserStoreModule.class, UserAgentAuthMatchModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerVerificationStateManagerTest_TestComponent.create();
        }

        VerificationStateManager verificationStateManager();

        AuthMatchDataExtractor authMatchDataExtractor();
    }
}
