package org.example.age.common.avs.store;

import java.util.Optional;

/** Persistent store for {@link SiteConfig}'s. */
public interface SiteConfigStore {

    /** Loads the configuration for a site, if present. */
    Optional<SiteConfig> tryLoad(String siteId);

    /** Saves the configuration for a site. */
    void save(SiteConfig siteConfig);

    /** Deletes the configuration for a site. */
    void delete(String siteId);
}
