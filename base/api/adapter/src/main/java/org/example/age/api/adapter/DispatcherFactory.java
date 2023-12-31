package org.example.age.api.adapter;

import io.github.mikewacker.drift.api.Dispatcher;

/** Creates a {@link Dispatcher} from the underlying exchange. */
@FunctionalInterface
public interface DispatcherFactory<E> {

    Dispatcher create(E exchange);
}
