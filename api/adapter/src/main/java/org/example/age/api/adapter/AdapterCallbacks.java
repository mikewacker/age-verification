package org.example.age.api.adapter;

import java.util.List;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;

/** Repository of {@link AdapterCallback}'s that can be chained together. */
final class AdapterCallbacks {

    /** Chains the callbacks together, returning a single callback. */
    public static <E, S extends Sender, A1, A2, A3, A4> AdapterCallback<E, S, A1, A2, A3, A4> chain(
            AdapterCallback<E, S, A1, A2, A3, A4> invoker,
            List<AdapterCallback.Chained<E, S, A1, A2, A3, A4>> callbackChain) {
        AdapterCallback<E, S, A1, A2, A3, A4> callback = invoker;
        for (int index = callbackChain.size() - 1; index >= 0; index--) {
            AdapterCallback.Chained<E, S, A1, A2, A3, A4> chainedCallback = callbackChain.get(index);
            chainedCallback.setNext(callback);
            callback = chainedCallback;
        }
        return callback;
    }

    /** Creates an {@link AdapterCallback} that invokes an {@link ApiHandler.ZeroArg}. */
    public static <E, S extends Sender> AdapterCallback<E, S, Void, Void, Void, Void> zeroArgInvoker(
            ApiHandler.ZeroArg<S> apiHandler) {
        return new ZeroArgInvoker<>(apiHandler);
    }

    /** Creates an {@link AdapterCallback} that invokes an {@link ApiHandler.OneArg}. */
    public static <E, S extends Sender, A> AdapterCallback<E, S, A, Void, Void, Void> oneArgInvoker(
            ApiHandler.OneArg<S, A> apiHandler) {
        return new OneArgInvoker<>(apiHandler);
    }

    /** Creates an {@link AdapterCallback} that invokes an {@link ApiHandler.TwoArg}. */
    public static <E, S extends Sender, A1, A2> AdapterCallback<E, S, A1, A2, Void, Void> twoArgInvoker(
            ApiHandler.TwoArg<S, A1, A2> apiHandler) {
        return new TwoArgInvoker<>(apiHandler);
    }

    /** Creates an {@link AdapterCallback} that invokes an {@link ApiHandler.ThreeArg}. */
    public static <E, S extends Sender, A1, A2, A3> AdapterCallback<E, S, A1, A2, A3, Void> threeArgInvoker(
            ApiHandler.ThreeArg<S, A1, A2, A3> apiHandler) {
        return new ThreeArgInvoker<>(apiHandler);
    }

    /** Creates an {@link AdapterCallback} that invokes an {@link ApiHandler.FourArg}. */
    public static <E, S extends Sender, A1, A2, A3, A4> AdapterCallback<E, S, A1, A2, A3, A4> fourArgInvoker(
            ApiHandler.FourArg<S, A1, A2, A3, A4> apiHandler) {
        return new FourArgInvoker<>(apiHandler);
    }

    /**
     * Creates an {@link AdapterCallback.Chained} that creates the {@link Sender} and the {@link Dispatcher}.
     *
     * <p>This callback must run first; subsequent callbacks may use the {@link Sender}.</p>
     */
    public static <E, S extends Sender, A1, A2, A3, A4> AdapterCallback.Chained<E, S, A1, A2, A3, A4> init(
            SenderFactory<E, S> senderFactory, DispatcherFactory<E> dispatcherFactory) {
        return new InitCallback<>(senderFactory, dispatcherFactory);
    }

    /** Creates an {@link AdapterCallback.Chained} that extracts the first argument. */
    public static <E, S extends Sender, A1, A2, A3, A4> AdapterCallback.Chained<E, S, A1, A2, A3, A4> arg1(
            Extractor.Async<E, A1> arg1Extractor) {
        return new Arg1Callback<>(arg1Extractor);
    }

    /** Creates an {@link AdapterCallback.Chained} that extracts the second argument. */
    public static <E, S extends Sender, A1, A2, A3, A4> AdapterCallback.Chained<E, S, A1, A2, A3, A4> arg2(
            Extractor.Async<E, A2> arg2Extractor) {
        return new Arg2Callback<>(arg2Extractor);
    }

    /** Creates an {@link AdapterCallback.Chained} that extracts the third argument. */
    public static <E, S extends Sender, A1, A2, A3, A4> AdapterCallback.Chained<E, S, A1, A2, A3, A4> arg3(
            Extractor.Async<E, A3> arg3Extractor) {
        return new Arg3Callback<>(arg3Extractor);
    }

    /** Creates an {@link AdapterCallback.Chained} that extracts the fourth argument. */
    public static <E, S extends Sender, A1, A2, A3, A4> AdapterCallback.Chained<E, S, A1, A2, A3, A4> arg4(
            Extractor.Async<E, A4> arg4Extractor) {
        return new Arg4Callback<>(arg4Extractor);
    }

    // static class
    private AdapterCallbacks() {}

    /** Terminal callback that invokes an {@link ApiHandler.ZeroArg}. */
    private record ZeroArgInvoker<E, S extends Sender>(ApiHandler.ZeroArg<S> apiHandler)
            implements AdapterCallback<E, S, Void, Void, Void, Void> {

        @Override
        public void handleRequest(E exchange, ApiRequest<S, Void, Void, Void, Void> apiRequest) throws Exception {
            apiHandler.handleRequest(apiRequest.sender, apiRequest.dispatcher);
        }
    }

    /** Terminal callback that invokes an {@link ApiHandler.OneArg}. */
    private record OneArgInvoker<E, S extends Sender, A>(ApiHandler.OneArg<S, A> apiHandler)
            implements AdapterCallback<E, S, A, Void, Void, Void> {

        @Override
        public void handleRequest(E exchange, ApiRequest<S, A, Void, Void, Void> apiRequest) throws Exception {
            apiHandler.handleRequest(apiRequest.sender, apiRequest.arg1, apiRequest.dispatcher);
        }
    }

    /** Terminal callback that invokes an {@link ApiHandler.TwoArg}. */
    private record TwoArgInvoker<E, S extends Sender, A1, A2>(ApiHandler.TwoArg<S, A1, A2> apiHandler)
            implements AdapterCallback<E, S, A1, A2, Void, Void> {

        @Override
        public void handleRequest(E exchange, ApiRequest<S, A1, A2, Void, Void> apiRequest) throws Exception {
            apiHandler.handleRequest(apiRequest.sender, apiRequest.arg1, apiRequest.arg2, apiRequest.dispatcher);
        }
    }

    /** Terminal callback that invokes an {@link ApiHandler.ThreeArg}. */
    private record ThreeArgInvoker<E, S extends Sender, A1, A2, A3>(ApiHandler.ThreeArg<S, A1, A2, A3> apiHandler)
            implements AdapterCallback<E, S, A1, A2, A3, Void> {

        @Override
        public void handleRequest(E exchange, ApiRequest<S, A1, A2, A3, Void> apiRequest) throws Exception {
            apiHandler.handleRequest(
                    apiRequest.sender, apiRequest.arg1, apiRequest.arg2, apiRequest.arg3, apiRequest.dispatcher);
        }
    }

    /** Terminal callback that invokes an {@link ApiHandler.FourArg}. */
    private record FourArgInvoker<E, S extends Sender, A1, A2, A3, A4>(ApiHandler.FourArg<S, A1, A2, A3, A4> apiHandler)
            implements AdapterCallback<E, S, A1, A2, A3, A4> {

        @Override
        public void handleRequest(E exchange, ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception {
            apiHandler.handleRequest(
                    apiRequest.sender,
                    apiRequest.arg1,
                    apiRequest.arg2,
                    apiRequest.arg3,
                    apiRequest.arg4,
                    apiRequest.dispatcher);
        }
    }

    /** Chained callback the creates the {@link Sender} and the {@link Dispatcher}. */
    private static final class InitCallback<E, S extends Sender, A1, A2, A3, A4>
            extends AdapterCallback.Chained<E, S, A1, A2, A3, A4> {

        private final SenderFactory<E, S> senderFactory;
        private final DispatcherFactory<E> dispatcherFactory;

        @Override
        public void handleRequest(E exchange, ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception {
            apiRequest.sender = senderFactory.create(exchange);
            apiRequest.dispatcher = dispatcherFactory.create(exchange);
            next.handleRequest(exchange, apiRequest);
        }

        private InitCallback(SenderFactory<E, S> senderFactory, DispatcherFactory<E> dispatcherFactory) {
            this.senderFactory = senderFactory;
            this.dispatcherFactory = dispatcherFactory;
        }
    }

    /** Chained callback that extracts an argument. */
    private abstract static class ArgCallback<E, S extends Sender, A1, A2, A3, A4, A>
            extends AdapterCallback.Chained<E, S, A1, A2, A3, A4> {

        private final Extractor.Async<E, A> argExtractor;

        @Override
        public final void handleRequest(E exchange, ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception {
            argExtractor.tryExtract(exchange, maybeArg -> onArgExtracted(exchange, apiRequest, maybeArg));
        }

        /** Sets the argument in the {@link ApiRequest}. */
        protected abstract void setArg(ApiRequest<S, A1, A2, A3, A4> apiRequest, A arg);

        protected ArgCallback(Extractor.Async<E, A> argExtractor) {
            this.argExtractor = argExtractor;
        }

        /** Called when the argument has been extracted. */
        private void onArgExtracted(E exchange, ApiRequest<S, A1, A2, A3, A4> apiRequest, HttpOptional<A> maybeArg)
                throws Exception {
            if (maybeArg.isEmpty()) {
                apiRequest.sender.sendErrorCode(maybeArg.statusCode());
                return;
            }
            A arg = maybeArg.get();

            setArg(apiRequest, arg);
            next.handleRequest(exchange, apiRequest);
        }
    }

    /** Chained callback that extracts the first argument. */
    private static final class Arg1Callback<E, S extends Sender, A1, A2, A3, A4>
            extends ArgCallback<E, S, A1, A2, A3, A4, A1> {

        @Override
        protected void setArg(ApiRequest<S, A1, A2, A3, A4> apiRequest, A1 arg1) {
            apiRequest.arg1 = arg1;
        }

        private Arg1Callback(Extractor.Async<E, A1> arg1Extractor) {
            super(arg1Extractor);
        }
    }

    /** Chained callback that extracts the second argument. */
    private static final class Arg2Callback<E, S extends Sender, A1, A2, A3, A4>
            extends ArgCallback<E, S, A1, A2, A3, A4, A2> {

        @Override
        protected void setArg(ApiRequest<S, A1, A2, A3, A4> apiRequest, A2 arg2) {
            apiRequest.arg2 = arg2;
        }

        private Arg2Callback(Extractor.Async<E, A2> arg2Extractor) {
            super(arg2Extractor);
        }
    }

    /** Chained callback that extracts the third argument. */
    private static final class Arg3Callback<E, S extends Sender, A1, A2, A3, A4>
            extends ArgCallback<E, S, A1, A2, A3, A4, A3> {

        @Override
        protected void setArg(ApiRequest<S, A1, A2, A3, A4> apiRequest, A3 arg3) {
            apiRequest.arg3 = arg3;
        }

        private Arg3Callback(Extractor.Async<E, A3> arg3Extractor) {
            super(arg3Extractor);
        }
    }

    /** Chained callback that extracts the fourth argument. */
    private static final class Arg4Callback<E, S extends Sender, A1, A2, A3, A4>
            extends ArgCallback<E, S, A1, A2, A3, A4, A4> {

        @Override
        protected void setArg(ApiRequest<S, A1, A2, A3, A4> apiRequest, A4 arg4) {
            apiRequest.arg4 = arg4;
        }

        private Arg4Callback(Extractor.Async<E, A4> arg4Extractor) {
            super(arg4Extractor);
        }
    }
}
