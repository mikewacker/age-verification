package org.example.age.service.module.location.common;

import org.example.age.service.location.common.Location;

/**
 * Provides a {@link Location} for the age verification service.
 *
 * <p>The location provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableAvsLocationProvider {

    Location getAvs();
}
