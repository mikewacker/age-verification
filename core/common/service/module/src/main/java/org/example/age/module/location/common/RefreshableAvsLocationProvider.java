package org.example.age.module.location.common;

/**
 * Provides an {@link AvsLocation}.
 *
 * <p>The location provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableAvsLocationProvider {

    AvsLocation get();
}
