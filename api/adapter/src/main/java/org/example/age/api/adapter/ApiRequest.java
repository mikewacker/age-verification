package org.example.age.api.adapter;

import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;

/**
 * Internal data structure for an API request.
 *
 * <p>Some type parameters may be {@link Void}, depending on how many arguments the API request actually has.</p>
 */
final class ApiRequest<S extends Sender, A1, A2, A3, A4> {
    public S sender = null;
    public Dispatcher dispatcher = null;
    public A1 arg1 = null;
    public A2 arg2 = null;
    public A3 arg3 = null;
    public A4 arg4 = null;
}
