package org.example.age.service;

import org.immutables.value.Value;

/** Style annotation for configuration interfaces. */
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE, overshadowImplementation = true)
public @interface ConfigStyle {}
