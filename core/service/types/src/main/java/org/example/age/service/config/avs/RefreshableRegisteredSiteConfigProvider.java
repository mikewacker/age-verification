package org.example.age.service.config.avs;

import java.util.Optional;

/**
 * Provides {@link RegisteredSiteConfig} for each registered site, or empty for an unregistered site.
 *
 * <p>The config provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableRegisteredSiteConfigProvider {

    Optional<RegisteredSiteConfig> tryGet(String siteId);
}
