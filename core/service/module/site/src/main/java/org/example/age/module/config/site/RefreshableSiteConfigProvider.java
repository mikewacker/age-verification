package org.example.age.module.config.site;

/**
 * Provides {@link SiteConfig}.
 *
 * <p>The config provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableSiteConfigProvider {

    SiteConfig get();
}
