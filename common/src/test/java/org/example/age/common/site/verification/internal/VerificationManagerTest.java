package org.example.age.common.site.verification.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import java.time.Duration;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;
import org.assertj.core.data.Offset;
import org.example.age.common.site.store.InMemoryVerificationStoreModule;
import org.example.age.common.site.store.VerificationState;
import org.example.age.common.site.store.VerificationStatus;
import org.example.age.common.site.store.VerificationStore;
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

public final class VerificationManagerTest {

    private VerificationManager verificationManager;
    private VerificationStore userStore;

    private static SecureId pseudonymKey;
    private static final Duration EXPIRES_IN = Duration.ofHours(1);

    @BeforeEach
    public void createVerificationManagerEtAl() {
        TestComponent component = TestComponent.create();
        verificationManager = component.verificationManager();
        userStore = component.verificationStore();
    }

    @BeforeAll
    public static void generateKeys() {
        pseudonymKey = SecureId.generate();
    }

    @Test
    public void verify() {
        HttpServerExchange exchange = createStubExchange();
        VerificationSession session = createSession();
        verificationManager.onVerificationSessionReceived("username", session, exchange);

        VerifiedUser user = createUser();
        AgeCertificate certificate = createCertificate(session, user);
        int statusCode = verificationManager.onAgeCertificateReceived(certificate);
        assertThat(statusCode).isEqualTo(StatusCodes.OK);

        VerificationState state = userStore.load("username");
        assertThat(state.status()).isEqualTo(VerificationStatus.VERIFIED);
        VerifiedUser expectedUser = user.localize(pseudonymKey);
        assertThat(state.verifiedUser()).isEqualTo(expectedUser);
        long now = System.currentTimeMillis() / 1000;
        long expectedExpiration = now + EXPIRES_IN.toSeconds();
        assertThat(state.expiration()).isCloseTo(expectedExpiration, Offset.offset(1L));
    }

    @Test
    public void failToVerify_DuplicateVerification() {
        HttpServerExchange exchange1 = createStubExchange();
        VerificationSession session1 = createSession();
        verificationManager.onVerificationSessionReceived("username1", session1, exchange1);

        VerifiedUser user = createUser();
        AgeCertificate certificate1 = createCertificate(session1, user);
        int statusCode1 = verificationManager.onAgeCertificateReceived(certificate1);
        assertThat(statusCode1).isEqualTo(StatusCodes.OK);

        HttpServerExchange exchange2 = createStubExchange();
        VerificationSession session2 = createSession();
        verificationManager.onVerificationSessionReceived("username2", session2, exchange2);

        AgeCertificate certificate2 = createCertificate(session2, user);
        int statusCode2 = verificationManager.onAgeCertificateReceived(certificate2);
        assertThat(statusCode2).isEqualTo(StatusCodes.CONFLICT);
    }

    @Test
    public void failToVerify_PendingVerificationNotFound() {
        VerificationSession session = createSession();
        VerifiedUser user = createUser();
        AgeCertificate certificate = createCertificate(session, user);
        int statusCode = verificationManager.onAgeCertificateReceived(certificate);
        assertThat(statusCode).isEqualTo(StatusCodes.NOT_FOUND);
    }

    private static HttpServerExchange createStubExchange() {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addStubIoThread(exchange);
        return exchange;
    }

    private static VerificationSession createSession() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(5));
        AuthKey authKey = AuthKey.ofBytes(new byte[32]);
        return VerificationSession.of(request, authKey);
    }

    private static VerifiedUser createUser() {
        return VerifiedUser.of(SecureId.generate(), 18);
    }

    private AgeCertificate createCertificate(VerificationSession session, VerifiedUser user) {
        VerificationRequest request = session.verificationRequest();
        AuthToken authToken = AuthToken.empty();
        return AgeCertificate.of(request, user, authToken);
    }

    /** Dagger module that binds dependencies needed to create a {@link VerificationManager}. */
    @Module(
            includes = {
                VerificationManagerModule.class,
                InMemoryVerificationStoreModule.class,
                InMemoryPendingStoreFactoryModule.class,
            })
    interface TestModule {

        @Provides
        @Named("pseudonymKey")
        @Singleton
        static Supplier<SecureId> providePseudonymKey() {
            return () -> pseudonymKey;
        }

        @Provides
        @Named("expiresIn")
        @Singleton
        static Supplier<Duration> provideExpiresIn() {
            return () -> EXPIRES_IN;
        }
    }

    /** Dagger component that provides a {@link VerificationManager}, and also a {@link VerificationStore}. */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerVerificationManagerTest_TestComponent.create();
        }

        VerificationManager verificationManager();

        VerificationStore verificationStore();
    }
}
