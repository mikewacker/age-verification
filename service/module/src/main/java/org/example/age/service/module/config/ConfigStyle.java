package org.example.age.service.module.config;

import org.immutables.value.Generated;
import org.immutables.value.Value;

/** Style annotation for configuration interfaces. */
@Value.Style(
        visibility = Value.Style.ImplementationVisibility.PACKAGE,
        overshadowImplementation = true,
        jdk9Collections = true,
        allowedClasspathAnnotations = {Generated.class, javax.annotation.processing.Generated.class},
        defaults = @Value.Immutable(copy = false),
        from = "")
public @interface ConfigStyle {}
