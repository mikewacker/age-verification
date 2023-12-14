package org.example.age.module.location.common;

import java.util.NoSuchElementException;

/**
 * Provides a {@link SiteLocation} for each registered site.
 *
 * <p>The locations provided may be refreshed.</p>
 *
 * <p>It is not expected that a location would be retrieved an unregistered site;
 * if this occurs, a {@link NoSuchElementException} will be thrown.</p>
 */
@FunctionalInterface
public interface RefreshableSiteLocationProvider {

    SiteLocation get(String siteId);
}
