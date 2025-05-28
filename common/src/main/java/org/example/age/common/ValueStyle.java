package org.example.age.common;

import org.immutables.value.Generated;
import org.immutables.value.Value;

/** Style annotation for value types. */
@Value.Style(
        visibility = Value.Style.ImplementationVisibility.PACKAGE,
        overshadowImplementation = true,
        jdk9Collections = true,
        allowedClasspathAnnotations = {Generated.class, javax.annotation.processing.Generated.class},
        defaults = @Value.Immutable(copy = false),
        from = "")
public @interface ValueStyle {}
