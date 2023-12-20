package org.example.age.service.location;

import java.util.NoSuchElementException;

/**
 * Provides a {@link Location} for each registered site.
 *
 * <p>The locations provided may be refreshed.</p>
 *
 * <p>It is not expected that a location would be retrieved an unregistered site;
 * if this occurs, a {@link NoSuchElementException} will be thrown.</p>
 */
@FunctionalInterface
public interface RefreshableSiteLocationProvider {

    Location getSite(String siteId);
}
