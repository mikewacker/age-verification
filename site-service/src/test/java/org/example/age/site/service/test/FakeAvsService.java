package org.example.age.site.service.test;

import java.security.PrivateKey;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.example.age.api.CodeSender;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.api.SiteLocation;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.SecureId;
import org.example.age.infra.service.client.RequestDispatcher;

/** Fake service for {@link AvsApi}. */
@Singleton
@SuppressWarnings("UnusedVariable")
final class FakeAvsService implements AvsApi {

    private final RequestDispatcher requestDispatcher;
    private final Provider<SiteLocation> siteLocationProvider;
    private final Provider<PrivateKey> privateSigningKeyProvider;

    @Inject
    public FakeAvsService(
            RequestDispatcher requestDispatcher,
            Provider<SiteLocation> siteLocationProvider,
            @Named("signing") Provider<PrivateKey> privateSigningKeyProvider) {
        this.requestDispatcher = requestDispatcher;
        this.siteLocationProvider = siteLocationProvider;
        this.privateSigningKeyProvider = privateSigningKeyProvider;
    }

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String siteId, Dispatcher dispatcher) {}

    @Override
    public void linkVerificationRequest(
            CodeSender sender, String accountId, SecureId requestId, Dispatcher dispatcher) {}

    @Override
    public void sendAgeCertificate(
            CodeSender sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {}
}
