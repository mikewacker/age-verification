package org.example.age.api.base;

/** Asynchronous API handler that can send a response or dispatch the request. */
public interface ApiHandler {

    /** API handler for a request with zero arguments. */
    @FunctionalInterface
    interface ZeroArg<S extends Sender> {

        void handleRequest(S sender, Dispatcher dispatcher) throws Exception;
    }

    /** API handler for a request with one argument. */
    @FunctionalInterface
    interface OneArg<S extends Sender, A> {

        void handleRequest(S sender, A arg, Dispatcher dispatcher) throws Exception;
    }

    /** API handler for a request with two arguments. */
    @FunctionalInterface
    interface TwoArg<S extends Sender, A1, A2> {

        void handleRequest(S sender, A1 arg1, A2 arg2, Dispatcher dispatcher) throws Exception;
    }

    /** API handler for a request with three arguments. */
    @FunctionalInterface
    interface ThreeArg<S extends Sender, A1, A2, A3> {

        void handleRequest(S sender, A1 arg1, A2 arg2, A3 arg3, Dispatcher dispatcher) throws Exception;
    }

    /** API handler for a request with four arguments. */
    @FunctionalInterface
    interface FourArg<S extends Sender, A1, A2, A3, A4> {

        void handleRequest(S sender, A1 arg1, A2 arg2, A3 arg3, A4 arg4, Dispatcher dispatcher) throws Exception;
    }
}
