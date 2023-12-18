package org.example.age.service.config.avs;

/**
 * Provides {@link AvsConfig}.
 *
 * <p>The config provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableAvsConfigProvider {

    AvsConfig get();
}
