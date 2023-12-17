package org.example.age.service.module.config.avs;

import org.example.age.service.config.avs.AvsConfig;

/**
 * Provides {@link AvsConfig}.
 *
 * <p>The config provided may be refreshed.</p>
 */
@FunctionalInterface
public interface RefreshableAvsConfigProvider {

    AvsConfig get();
}
