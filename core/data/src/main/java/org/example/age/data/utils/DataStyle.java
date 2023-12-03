package org.example.age.data.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.processing.Generated;
import org.immutables.value.Value;

/** Style to apply to immutable data types. */
@Value.Style(
        visibility = Value.Style.ImplementationVisibility.PACKAGE,
        overshadowImplementation = true,
        defaults = @Value.Immutable(copy = false),
        from = "",
        allowedClasspathAnnotations = {Generated.class, org.immutables.value.Generated.class})
@Target(ElementType.TYPE)
public @interface DataStyle {}
