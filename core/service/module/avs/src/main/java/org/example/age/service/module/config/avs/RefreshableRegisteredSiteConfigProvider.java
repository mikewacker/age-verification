package org.example.age.service.module.config.avs;

import java.util.Optional;
import org.example.age.service.config.avs.RegisteredSiteConfig;

/**
 * Provides {@link RegisteredSiteConfig} for each registered site, or empty for an unregistered site.
 *
 * <p>The config provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableRegisteredSiteConfigProvider {

    Optional<RegisteredSiteConfig> tryGet(String siteId);
}
