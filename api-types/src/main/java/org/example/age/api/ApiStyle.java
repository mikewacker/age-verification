package org.example.age.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import javax.annotation.processing.Generated;
import org.immutables.value.Value;

/**
 * <code>@{@link Value.Style}</code> for <code>@{@link Value.Immutable}</code> types that are serializable as JSON.
 *
 * <p>Types will still need to be annotated with <code>@{@link JsonDeserialize}(as = Immutable[Type].class)</code>.</p>
 */
@Value.Style(
        visibility = Value.Style.ImplementationVisibility.PACKAGE,
        overshadowImplementation = true,
        defaults = @Value.Immutable(copy = false),
        from = "",
        allowedClasspathAnnotations = {Generated.class, org.immutables.value.Generated.class})
@JsonSerialize
@Target(ElementType.TYPE)
public @interface ApiStyle {}