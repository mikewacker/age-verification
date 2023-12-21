package org.example.age.module.internal.resource;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

public final class DoubleCheckedProviderTest {

    @Test
    public void get() {
        Supplier<Object> valueProvider = DoubleCheckedProvider.create(Object::new);
        Object value1 = valueProvider.get();
        Object value2 = valueProvider.get();
        assertThat(value1).isSameAs(value2);
    }
}
