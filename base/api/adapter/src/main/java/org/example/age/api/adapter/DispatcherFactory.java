package org.example.age.api.adapter;

import org.example.age.api.base.Dispatcher;

/** Creates a {@link Dispatcher} from the underlying exchange. */
@FunctionalInterface
public interface DispatcherFactory<E> {

    Dispatcher create(E exchange);
}
