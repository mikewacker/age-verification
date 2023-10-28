package org.example.age.common.avs.store;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;

/** In-memory {@link SiteConfigStore}. */
@Singleton
final class InMemorySiteConfigStore implements SiteConfigStore {

    private final Map<String, SiteConfig> siteConfigs = new ConcurrentHashMap<>();

    @Inject
    public InMemorySiteConfigStore() {}

    @Override
    public Optional<SiteConfig> tryLoad(String siteId) {
        return Optional.ofNullable(siteConfigs.get(siteId));
    }

    @Override
    public void save(SiteConfig siteConfig) {
        siteConfigs.put(siteConfig.siteId(), siteConfig);
    }

    @Override
    public void delete(String siteId) {
        siteConfigs.remove(siteId);
    }
}
