package org.example.age.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.immutables.value.Value;

/** Style to apply to immutable data types. */
@Target(ElementType.TYPE)
// Auto-discovery of annotations does not play nice with Java modules, so it is disabled.
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public @interface DataStyle {}
