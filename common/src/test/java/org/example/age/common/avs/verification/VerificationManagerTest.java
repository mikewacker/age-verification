package org.example.age.common.avs.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.google.common.net.HostAndPort;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import io.undertow.server.HttpServerExchange;
import java.time.Duration;
import java.util.function.Supplier;
import javax.inject.Named;
import javax.inject.Singleton;
import org.assertj.core.data.Offset;
import org.example.age.common.avs.store.InMemorySiteConfigStoreModule;
import org.example.age.common.avs.store.InMemoryVerifiedUserStoreModule;
import org.example.age.common.avs.store.SiteConfig;
import org.example.age.common.avs.store.SiteConfigStore;
import org.example.age.common.avs.store.VerifiedUserStore;
import org.example.age.common.avs.verification.internal.Verification;
import org.example.age.common.avs.verification.internal.VerificationManager;
import org.example.age.common.avs.verification.internal.VerificationManagerModule;
import org.example.age.common.base.auth.DisabledAuthMatchDataExtractorModule;
import org.example.age.common.base.store.InMemoryPendingStoreFactoryModule;
import org.example.age.common.base.utils.internal.HttpOptional;
import org.example.age.data.AgeThresholds;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.testing.TestExchanges;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class VerificationManagerTest {

    private VerificationManager verificationManager;

    private static SecureId pseudonymKey;
    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);

    @BeforeEach
    public void createVerificationManager() {
        TestComponent component = TestComponent.create();
        verificationManager = component.verificationManager();
        initVerifiedUserStore(component.verifiedUserStore());
        initSiteConfigStore(component.siteConfigStore());
    }

    @BeforeAll
    public static void generateKeys() {
        pseudonymKey = SecureId.generate();
    }

    @Test
    public void createVerificationSession() {
        HttpServerExchange exchange = createStubExchange();
        HttpOptional<VerificationSession> maybeSession =
                verificationManager.createVerificationSession("Site", exchange);
        assertThat(maybeSession.isPresent()).isTrue();
        VerificationSession session = maybeSession.get();
        assertThat(session.verificationRequest().siteId()).isEqualTo("Site");
        long now = System.currentTimeMillis() / 1000;
        long expectedExpiration = now + EXPIRES_IN.toSeconds();
        assertThat(session.verificationRequest().expiration()).isCloseTo(expectedExpiration, Offset.offset(1L));
    }

    @Test
    public void verify() {
        HttpServerExchange sessionExchange = createStubExchange();
        HttpOptional<VerificationSession> maybeSession =
                verificationManager.createVerificationSession("Site", sessionExchange);
        assertThat(maybeSession.isPresent()).isTrue();
        SecureId requestId = maybeSession.get().verificationRequest().id();

        HttpServerExchange linkExchange = createStubExchange();
        int statusCode = verificationManager.linkVerificationRequest("name", requestId, linkExchange);
        assertThat(statusCode).isEqualTo(200);

        HttpServerExchange certificateExchange = createStubExchange();
        HttpOptional<Verification> maybeVerification =
                verificationManager.createAgeCertificate("name", certificateExchange);
        assertThat(maybeVerification.isPresent()).isTrue();
    }

    @Test
    public void error_LinkVerificationSessionTwice() {
        HttpServerExchange sessionExchange = createStubExchange();
        HttpOptional<VerificationSession> maybeSession =
                verificationManager.createVerificationSession("Site", sessionExchange);
        assertThat(maybeSession.isPresent()).isTrue();
        SecureId requestId = maybeSession.get().verificationRequest().id();

        HttpServerExchange linkExchange1 = createStubExchange();
        int statusCode1 = verificationManager.linkVerificationRequest("name", requestId, linkExchange1);
        assertThat(statusCode1).isEqualTo(200);

        HttpServerExchange linkExchange2 = createStubExchange();
        int statusCode2 = verificationManager.linkVerificationRequest("other name", requestId, linkExchange2);
        assertThat(statusCode2).isEqualTo(404);
    }

    @Test
    public void error_UnverifiedAccount() {
        HttpServerExchange linkExchange = createStubExchange();
        SecureId requestId = SecureId.generate();
        int statusCode = verificationManager.linkVerificationRequest("unverified", requestId, linkExchange);
        assertThat(statusCode).isEqualTo(403);

        HttpServerExchange certificateExchange = createStubExchange();
        HttpOptional<Verification> maybeVerification =
                verificationManager.createAgeCertificate("unverified", certificateExchange);
        assertThat(maybeVerification.statusCode()).isEqualTo(403);
    }

    @Test
    public void error_VerificationSessionNotFound() {
        HttpServerExchange linkExchange = createStubExchange();
        SecureId requestId = SecureId.generate();
        int statusCode = verificationManager.linkVerificationRequest("name", requestId, linkExchange);
        assertThat(statusCode).isEqualTo(404);

        HttpServerExchange certificateExchange = createStubExchange();
        HttpOptional<Verification> maybeVerification =
                verificationManager.createAgeCertificate("name", certificateExchange);
        assertThat(maybeVerification.statusCode()).isEqualTo(404);
    }

    @Test
    public void error_SiteNotFound() {
        HttpServerExchange exchange = createStubExchange();
        HttpOptional<VerificationSession> maybeSession = verificationManager.createVerificationSession("DNE", exchange);
        assertThat(maybeSession.statusCode()).isEqualTo(404);
    }

    private static HttpServerExchange createStubExchange() {
        HttpServerExchange exchange = mock(HttpServerExchange.class);
        TestExchanges.addStubIoThread(exchange);
        return exchange;
    }

    private static void initVerifiedUserStore(VerifiedUserStore userStore) {
        VerifiedUser user1 = VerifiedUser.of(SecureId.generate(), 18);
        userStore.trySave("name", user1);
        VerifiedUser user2 = VerifiedUser.of(SecureId.generate(), 18);
        userStore.trySave("other name", user2);
    }

    private static void initSiteConfigStore(SiteConfigStore siteConfigStore) {
        SiteConfig siteConfig = SiteConfig.builder("Site")
                .siteLocation(HostAndPort.fromParts("localhost", 80))
                .ageThresholds(AgeThresholds.of(18))
                .pseudonymKey(pseudonymKey)
                .build();
        siteConfigStore.save(siteConfig);
    }

    /** Dagger module that binds dependencies needed to create a {@link VerificationManager}. */
    @Module(
            includes = {
                VerificationManagerModule.class,
                InMemoryVerifiedUserStoreModule.class,
                InMemorySiteConfigStoreModule.class,
                DisabledAuthMatchDataExtractorModule.class,
                InMemoryPendingStoreFactoryModule.class,
            })
    interface TestModule {

        @Provides
        @Named("expiresIn")
        @Singleton
        static Supplier<Duration> provideExpiresIn() {
            return () -> EXPIRES_IN;
        }
    }

    /**
     * Dagger component that provides a {@link VerificationManager},
     * and also a {@link VerifiedUserStore} and a {@link SiteConfigStore}.
     */
    @Component(modules = TestModule.class)
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerVerificationManagerTest_TestComponent.create();
        }

        VerificationManager verificationManager();

        VerifiedUserStore verifiedUserStore();

        SiteConfigStore siteConfigStore();
    }
}
