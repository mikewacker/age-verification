package org.example.age.common.avs.store;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.common.avs.config.RegisteredSiteConfig;

/** In-memory {@link RegisteredSiteConfigStore}. */
@Singleton
final class InMemoryRegisteredSiteConfigStore implements RegisteredSiteConfigStore {

    private final Map<String, RegisteredSiteConfig> siteConfigs = new ConcurrentHashMap<>();

    @Inject
    public InMemoryRegisteredSiteConfigStore() {}

    @Override
    public Optional<RegisteredSiteConfig> tryLoad(String siteId) {
        return Optional.ofNullable(siteConfigs.get(siteId));
    }

    @Override
    public void save(RegisteredSiteConfig siteConfig) {
        siteConfigs.put(siteConfig.siteId(), siteConfig);
    }

    @Override
    public void delete(String siteId) {
        siteConfigs.remove(siteId);
    }
}
