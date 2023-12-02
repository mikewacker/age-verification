package org.example.age.common.avs.store;

import java.util.Optional;
import org.example.age.common.avs.config.RegisteredSiteConfig;

/** Persistent store for {@link RegisteredSiteConfig}'s. */
public interface RegisteredSiteConfigStore {

    /** Loads the configuration for a site, if present. */
    Optional<RegisteredSiteConfig> tryLoad(String siteId);

    /** Saves the configuration for a site. */
    void save(RegisteredSiteConfig siteConfig);

    /** Deletes the configuration for a site. */
    void delete(String siteId);
}
