package org.example.age.api.adapter;

import io.github.mikewacker.drift.api.Sender;

/** Creates a {@link Sender} from the underlying exchange. */
@FunctionalInterface
public interface SenderFactory<E, S extends Sender> {

    S create(E exchange);
}
