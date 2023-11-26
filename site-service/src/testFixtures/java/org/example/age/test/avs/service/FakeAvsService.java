package org.example.age.test.avs.service;

import java.security.PrivateKey;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.api.StatusCodeSender;
import org.example.age.avs.api.AvsApi;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptor;
import org.example.age.common.service.data.SiteLocation;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.data.crypto.AesGcmEncryptionPackage;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.infra.service.client.RequestDispatcher;

/** Fake service for {@link AvsApi}. */
@Singleton
final class FakeAvsService implements AvsApi {

    private final AuthMatchDataEncryptor authDataEncryptor;
    private final RequestDispatcher requestDispatcher;
    private final Provider<SiteLocation> siteLocationProvider;
    private final Provider<PrivateKey> privateSigningKeyProvider;

    private final Map<String, VerifiedUser> users = populateAccounts();

    private VerificationSession storedSession = null;
    private String storedAccountId = null;
    private VerifiedUser storedUser = null;

    @Inject
    public FakeAvsService(
            AuthMatchDataEncryptor authDataEncryptor,
            RequestDispatcher requestDispatcher,
            Provider<SiteLocation> siteLocationProvider,
            @Named("signing") Provider<PrivateKey> privateSigningKeyProvider) {
        this.authDataEncryptor = authDataEncryptor;
        this.requestDispatcher = requestDispatcher;
        this.siteLocationProvider = siteLocationProvider;
        this.privateSigningKeyProvider = privateSigningKeyProvider;
    }

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String siteId, Dispatcher dispatcher) {
        clearStoredVerificationData();
        VerificationRequest request = VerificationRequest.generateForSite(siteId, Duration.ofMinutes(5));
        storedSession = VerificationSession.create(request);
        sender.sendBody(storedSession);
    }

    @Override
    public void linkVerificationRequest(
            StatusCodeSender sender, String accountId, SecureId requestId, Dispatcher dispatcher) {
        if ((storedSession == null)
                || !requestId.equals(storedSession.verificationRequest().id())) {
            clearStoredVerificationData();
            sender.sendErrorCode(418);
            return;
        }

        storedAccountId = accountId;
        storedUser = users.get(accountId);
        if (storedUser == null) {
            clearStoredVerificationData();
            sender.sendErrorCode(418);
            return;
        }

        sender.sendOk();
    }

    @Override
    public void sendAgeCertificate(
            StatusCodeSender sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {
        if (!accountId.equals(storedAccountId)) {
            clearStoredVerificationData();
            sender.sendErrorCode(418);
            return;
        }

        SignedAgeCertificate signedCertificate = createSignedAgeCertificate(authData);
        clearStoredVerificationData();

        HttpUrl certificateUrl = siteLocationProvider.get().ageCertificateUrl();
        requestDispatcher
                .createExchangeBuilder(certificateUrl, sender, dispatcher)
                .post(signedCertificate)
                .dispatchWithoutResponseBody(this::onAgeCertificateResponseReceived);
    }

    /** Populates preset accounts for this service. */
    private static Map<String, VerifiedUser> populateAccounts() {
        VerifiedUser parent = VerifiedUser.of(SecureId.generate(), 40);
        VerifiedUser child = VerifiedUser.of(SecureId.generate(), 13, List.of(parent.pseudonym()));
        return Map.of("John Smith", parent, "Billy Smith", child);
    }

    /** Clears the stored verification data. */
    private void clearStoredVerificationData() {
        storedSession = null;
        storedAccountId = null;
        storedUser = null;
    }

    /** Creates a {@link SignedAgeCertificate} using the stored verification data. */
    private SignedAgeCertificate createSignedAgeCertificate(AuthMatchData authData) {
        VerificationRequest request = storedSession.verificationRequest();
        AesGcmEncryptionPackage authToken = authDataEncryptor.encrypt(authData, storedSession.authKey());
        AgeCertificate certificate = AgeCertificate.of(request, storedUser, authToken);
        return SignedAgeCertificate.sign(certificate, privateSigningKeyProvider.get());
    }

    /** Called when a response is received for the request to send a {@link SignedAgeCertificate} to a site. */
    private void onAgeCertificateResponseReceived(StatusCodeSender sender, Response response, Dispatcher dispatcher) {
        sender.send(response.code());
    }
}
