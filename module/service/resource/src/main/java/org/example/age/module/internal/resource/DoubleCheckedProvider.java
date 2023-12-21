package org.example.age.module.internal.resource;

import java.util.function.Supplier;

/** Singleton provider that uses double-checked locking. */
public final class DoubleCheckedProvider<V> implements Supplier<V> {

    private final Supplier<V> valueProvider;
    private volatile V value = null;
    private final Object lock = new Object();

    /** Creates a singleton provider. */
    public static <V> Supplier<V> create(Supplier<V> valueProvider) {
        return new DoubleCheckedProvider<>(valueProvider);
    }

    @Override
    public V get() {
        V localValue = value;
        if (localValue == null) {
            synchronized (lock) {
                localValue = value;
                if (localValue == null) {
                    localValue = valueProvider.get();
                    value = localValue;
                }
            }
        }
        return localValue;
    }

    private DoubleCheckedProvider(Supplier<V> valueProvider) {
        this.valueProvider = valueProvider;
    }
}
