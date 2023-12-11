package org.example.age.api.adapter;

import org.example.age.api.base.Sender;

/** Creates a {@link Sender} from the underlying exchange. */
@FunctionalInterface
public interface SenderFactory<E, S extends Sender> {

    S create(E exchange);
}
