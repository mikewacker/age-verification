package org.example.age.avs.endpoint;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.spi.AgeCertificateSigner;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.common.spi.AccountIdContext;

/** Endpoint for {@link AvsApi}. */
@Singleton
final class AvsEndpoint implements AvsApi {

    private final AccountIdContext accountIdContext;
    private final SiteClient.Repository siteClients;
    private final AvsVerificationManager verificationManager;
    private final AgeCertificateSigner ageCertificateSigner;

    @Inject
    public AvsEndpoint(
            AccountIdContext accountIdContext,
            SiteClient.Repository siteClients,
            AvsVerificationManager verificationManager,
            AgeCertificateSigner ageCertificateSigner) {
        this.accountIdContext = accountIdContext;
        this.siteClients = siteClients;
        this.verificationManager = verificationManager;
        this.ageCertificateSigner = ageCertificateSigner;
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequestForSite(String siteId) {
        siteClients.get(siteId); // check that the site is registered
        return verificationManager.createVerificationRequest(siteId);
    }

    @Override
    public CompletionStage<Void> linkVerificationRequest(SecureId requestId) {
        String accountId = accountIdContext.getForRequest();
        return verificationManager.linkVerificationRequest(requestId, accountId);
    }

    @Override
    public CompletionStage<Void> sendAgeCertificate() {
        String accountId = accountIdContext.getForRequest();
        return verificationManager
                .createAgeCertificate(accountId)
                .thenCompose(ageCertificateSigner::sign)
                .thenCompose(this::sendAgeCertificate);
    }

    /** Sends a signed age certificate to the corresponding site. */
    private CompletionStage<Void> sendAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        String siteId = signedAgeCertificate.getAgeCertificate().getRequest().getSiteId();
        SiteClient siteClient = siteClients.get(siteId);
        return siteClient.processAgeCertificate(signedAgeCertificate);
    }
}
