package org.example.age.module.client;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotFoundException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.example.age.api.client.SiteApi;
import org.example.age.service.module.client.SiteClientRepository;

/** Implementation of {@link SiteClientRepository}. */
@Singleton
final class SiteClientRepositoryImpl implements SiteClientRepository {

    private final Map<String, SiteApi> clients;

    @Inject
    public SiteClientRepositoryImpl(ServiceClientFactory clientFactory, AvsClientsConfig clientsConfig) {
        clients = createClients(clientFactory, clientsConfig);
    }

    @Override
    public SiteApi get(String siteId) {
        Optional<SiteApi> maybeClient = Optional.ofNullable(clients.get(siteId));
        return maybeClient.orElseThrow(NotFoundException::new);
    }

    /** Creates the clients. */
    private static Map<String, SiteApi> createClients(
            ServiceClientFactory clientFactory, AvsClientsConfig clientsConfig) {
        return clientsConfig.siteUrls().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, entry -> clientFactory.create(entry.getValue(), SiteApi.class)));
    }
}
