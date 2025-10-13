package org.example.age.avs.endpoint;

import com.google.common.collect.Maps;
import jakarta.inject.Inject;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.SignedAgeCertificate;
import org.example.age.common.api.client.AsyncCalls;
import org.example.age.site.api.client.SiteApi;
import retrofit2.Call;

/** Client for a site. */
final class SiteClient {

    private final SiteApi client;

    private SiteClient(SiteApi client) {
        this.client = client;
    }

    /** Processes a signed age certificate. */
    public CompletionStage<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
        Call<Void> call = client.processAgeCertificate(signedAgeCertificate);
        return AsyncCalls.make(call).exceptionallyCompose(SiteClient::onSendAgeCertificateError);
    }

    /** Handles an error processing the signed age certificate. */
    private static CompletionStage<Void> onSendAgeCertificateError(Throwable t) {
        Throwable error =
                ((t instanceof WebApplicationException e) && (e.getResponse().getStatus() == 404))
                        ? t
                        : new InternalServerErrorException(t);
        return CompletableFuture.failedFuture(error);
    }

    /** Repository of clients. */
    public static final class Repository {

        private final Map<String, SiteClient> clients;

        @Inject
        public Repository(Map<String, SiteApi> clients) {
            this.clients = Maps.transformValues(clients, SiteClient::new);
        }

        /** Gets a client, or throws {@link NotFoundException}. */
        public SiteClient get(String siteId) {
            return Optional.ofNullable(clients.get(siteId)).orElseThrow(NotFoundException::new);
        }
    }
}
