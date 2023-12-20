package org.example.age.service.location.common;

/**
 * Provides a {@link Location} for the age verification service.
 *
 * <p>The location provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableAvsLocationProvider {

    Location getAvs();
}
