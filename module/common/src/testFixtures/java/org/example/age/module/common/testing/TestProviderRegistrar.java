package org.example.age.module.common.testing;

/** Registers a provider with JAX-RS. */
@FunctionalInterface
public interface TestProviderRegistrar {

    void register(Object provider);
}
