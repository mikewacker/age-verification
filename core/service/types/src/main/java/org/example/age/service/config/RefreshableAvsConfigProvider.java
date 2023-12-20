package org.example.age.service.config;

/**
 * Provides {@link AvsConfig}.
 *
 * <p>The config provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableAvsConfigProvider {

    AvsConfig get();
}
