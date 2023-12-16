package org.example.age.service.module.location.common;

import org.example.age.service.location.common.AvsLocation;

/**
 * Provides an {@link AvsLocation}.
 *
 * <p>The location provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableAvsLocationProvider {

    AvsLocation get();
}
