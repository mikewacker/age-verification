package org.example.age.module.common.testing;

/** Registers a component with Jersey. */
@FunctionalInterface
public interface TestComponentRegistrar {

    void register(Object component);
}
