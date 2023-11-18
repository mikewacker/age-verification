package org.example.age.site.service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import okhttp3.RequestBody;
import org.example.age.api.CodeSender;
import org.example.age.api.Dispatcher;
import org.example.age.api.JsonSender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.certificate.SignedAgeCertificate;
import org.example.age.data.certificate.VerificationSession;
import org.example.age.infra.service.client.RequestDispatcher;
import org.example.age.site.api.AvsLocation;
import org.example.age.site.api.SiteApi;
import org.example.age.site.service.verification.internal.VerificationManager;

@Singleton
@SuppressWarnings("UnusedVariable")
final class SiteService implements SiteApi {

    private static RequestBody EMPTY_BODY = RequestBody.create(new byte[0]);

    private final VerificationManager verificationManager;
    private final RequestDispatcher requestDispatcher;
    private final Provider<AvsLocation> avsLocationProvider;
    private final Provider<String> siteIdProvider;

    @Inject
    public SiteService(
            VerificationManager verificationManager,
            RequestDispatcher requestDispatcher,
            Provider<AvsLocation> avsLocationProvider,
            @Named("siteId") Provider<String> siteIdProvider) {
        this.verificationManager = verificationManager;
        this.requestDispatcher = requestDispatcher;
        this.avsLocationProvider = avsLocationProvider;
        this.siteIdProvider = siteIdProvider;
    }

    @Override
    public void createVerificationSession(
            JsonSender<VerificationSession> sender, String accountId, AuthMatchData authData, Dispatcher dispatcher) {}

    @Override
    public void processAgeCertificate(
            CodeSender sender, SignedAgeCertificate signedCertificate, Dispatcher dispatcher) {}
}
