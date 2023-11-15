package org.example.age.data.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.immutables.value.Value;

/** Style to apply to immutable data types. */
@Target(ElementType.TYPE)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public @interface DataStyle {}
