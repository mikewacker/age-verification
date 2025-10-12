package org.example.age.site.endpoint;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.spi.AccountIdContext;
import org.example.age.site.api.SiteApi;
import org.example.age.site.api.VerificationState;

/** Endpoint for {@link SiteApi}. */
@Singleton
final class SiteEndpoint implements SiteApi {

    private final AccountIdContext accountIdContext;
    private final AvsClient avsClient;
    private final SiteVerificationManager verificationManager;
    private final AgeCertificateValidator ageCertificateValidator;

    @Inject
    public SiteEndpoint(
            AccountIdContext accountIdContext,
            AvsClient avsClient,
            SiteVerificationManager verificationManager,
            AgeCertificateValidator ageCertificateValidator) {
        this.accountIdContext = accountIdContext;
        this.avsClient = avsClient;
        this.verificationManager = verificationManager;
        this.ageCertificateValidator = ageCertificateValidator;
    }

    @Override
    public CompletionStage<VerificationState> getVerificationState() {
        String accountId = accountIdContext.getForRequest();
        return verificationManager.getVerificationState(accountId);
    }

    @Override
    public CompletionStage<VerificationRequest> createVerificationRequest() {
        String accountId = accountIdContext.getForRequest();
        return avsClient
                .createVerificationRequest()
                .thenCompose(request -> verificationManager.onVerificationRequestReceived(accountId, request));
    }

    @Override
    public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        return ageCertificateValidator
                .validate(signedAgeCertificate)
                .thenCompose(verificationManager::onAgeCertificateReceived);
    }
}
