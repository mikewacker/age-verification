package org.example.age.api.adapter;

import org.example.age.api.base.Sender;

/** Internal callback used to build an {@link ApiRequest} from the underlying exchange. */
@FunctionalInterface
interface AdapterCallback<E, S extends Sender, A1, A2, A3, A4> {

    void handleRequest(E exchange, ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception;

    /** {@link AdapterCallback} that is chained to another {@link AdapterCallback}. */
    abstract class Chained<E, S extends Sender, A1, A2, A3, A4> implements AdapterCallback<E, S, A1, A2, A3, A4> {

        protected AdapterCallback<E, S, A1, A2, A3, A4> next = null;

        public final void setNext(AdapterCallback<E, S, A1, A2, A3, A4> next) {
            this.next = next;
        }
    }
}
