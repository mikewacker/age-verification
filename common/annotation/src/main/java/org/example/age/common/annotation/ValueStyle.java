package org.example.age.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ValueStyle {}
