package org.example.age.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import org.immutables.value.Value;

@Target(ElementType.TYPE)
@Value.Style(visibility = Value.Style.ImplementationVisibility.PACKAGE)
public @interface PackageImplementation {}
