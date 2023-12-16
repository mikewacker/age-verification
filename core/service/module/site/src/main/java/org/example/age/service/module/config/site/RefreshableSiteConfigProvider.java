package org.example.age.service.module.config.site;

import org.example.age.service.config.site.SiteConfig;

/**
 * Provides {@link SiteConfig}.
 *
 * <p>The config provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableSiteConfigProvider {

    SiteConfig get();
}
