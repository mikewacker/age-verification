package org.example.age.api.adapter;

import io.github.mikewacker.drift.api.ApiHandler;
import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.Sender;

/**
 * Internal data structure for an API request.
 *
 * <p>Some type parameters may be {@link Void}, depending on how many arguments the API request actually has.</p>
 */
final class ApiRequest<S extends Sender, A1, A2, A3, A4> {

    private final S sender;
    private final Dispatcher dispatcher;
    private A1 arg1 = null;
    private A2 arg2 = null;
    private A3 arg3 = null;
    private A4 arg4 = null;

    /** Sets the first argument. */
    public void setArg1(A1 arg1) {
        this.arg1 = arg1;
    }

    /** Sets the second argument. */
    public void setArg2(A2 arg2) {
        this.arg2 = arg2;
    }

    /** Sets the third argument. */
    public void setArg3(A3 arg3) {
        this.arg3 = arg3;
    }

    /** Sets the fourth argument. */
    public void setArg4(A4 arg4) {
        this.arg4 = arg4;
    }

    private ApiRequest(S sender, Dispatcher dispatcher) {
        this.sender = sender;
        this.dispatcher = dispatcher;
    }

    /** Factory that creates a partially built {@link ApiRequest} from the underling exchange. */
    public interface Factory<E, S extends Sender> {

        /** Creates a {@link Factory} from the factories for the {@link Sender} and the {@link Dispatcher}. */
        static <E, S extends Sender> Factory<E, S> create(
                SenderFactory<E, S> senderFactory, DispatcherFactory<E> dispatcherFactory) {
            return new FactoryImpl<>(senderFactory, dispatcherFactory);
        }

        /** Creates a partially built {@link ApiRequest} from the underlying exchange. */
        <A1, A2, A3, A4> ApiRequest<S, A1, A2, A3, A4> create(E exchange);
    }

    /** Handler for an {@link ApiRequest}. */
    public interface Handler<S extends Sender, A1, A2, A3, A4> {

        /** Creates a {@link Handler} that invokes an {@link ApiHandler.ZeroArg}. */
        static <S extends Sender> Handler<S, Void, Void, Void, Void> zeroArg(ApiHandler.ZeroArg<S> apiHandler) {
            return new ZeroArgHandler<>(apiHandler);
        }

        /** Creates a {@link Handler} that invokes an {@link ApiHandler.OneArg}. */
        static <S extends Sender, A> Handler<S, A, Void, Void, Void> oneArg(ApiHandler.OneArg<S, A> apiHandler) {
            return new OneArgHandler<>(apiHandler);
        }

        /** Creates a {@link Handler} that invokes an {@link ApiHandler.TwoArg}. */
        static <S extends Sender, A1, A2> Handler<S, A1, A2, Void, Void> twoArg(
                ApiHandler.TwoArg<S, A1, A2> apiHandler) {
            return new TwoArgHandler<>(apiHandler);
        }

        /** Creates a {@link Handler} that invokes an {@link ApiHandler.ThreeArg}. */
        static <S extends Sender, A1, A2, A3> Handler<S, A1, A2, A3, Void> threeArg(
                ApiHandler.ThreeArg<S, A1, A2, A3> apiHandler) {
            return new ThreeArgHandler<>(apiHandler);
        }

        /** Creates a {@link Handler} that invokes an {@link ApiHandler.FourArg}. */
        static <S extends Sender, A1, A2, A3, A4> Handler<S, A1, A2, A3, A4> fourArg(
                ApiHandler.FourArg<S, A1, A2, A3, A4> apiHandler) {
            return new FourAgeHandler<>(apiHandler);
        }

        /** Sends an error status code if the {@link ApiRequest} cannot be built. */
        default void sendErrorCode(ApiRequest<S, A1, A2, A3, A4> apiRequest, int errorCode) {
            apiRequest.sender.sendErrorCode(errorCode);
        }

        /** Handles a fully built {@link ApiRequest}. */
        void handleRequest(ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception;
    }

    /** Internal {@link Factory} implementation. */
    private record FactoryImpl<E, S extends Sender>(
            SenderFactory<E, S> senderFactory, DispatcherFactory<E> dispatcherFactory) implements Factory<E, S> {

        @Override
        public <A1, A2, A3, A4> ApiRequest<S, A1, A2, A3, A4> create(E exchange) {
            S sender = senderFactory.create(exchange);
            Dispatcher dispatcher = dispatcherFactory.create(exchange);
            return new ApiRequest<>(sender, dispatcher);
        }
    }

    /** {@link Handler} that invokes an {@link ApiHandler.ZeroArg}. */
    private record ZeroArgHandler<S extends Sender>(ApiHandler.ZeroArg<S> apiHandler)
            implements Handler<S, Void, Void, Void, Void> {

        @Override
        public void handleRequest(ApiRequest<S, Void, Void, Void, Void> apiRequest) throws Exception {
            apiHandler.handleRequest(apiRequest.sender, apiRequest.dispatcher);
        }
    }

    /** {@link Handler} that invokes an {@link ApiHandler.OneArg}. */
    private record OneArgHandler<S extends Sender, A>(ApiHandler.OneArg<S, A> apiHandler)
            implements Handler<S, A, Void, Void, Void> {

        @Override
        public void handleRequest(ApiRequest<S, A, Void, Void, Void> apiRequest) throws Exception {
            apiHandler.handleRequest(apiRequest.sender, apiRequest.arg1, apiRequest.dispatcher);
        }
    }

    /** {@link Handler} that invokes an {@link ApiHandler.TwoArg}. */
    private record TwoArgHandler<S extends Sender, A1, A2>(ApiHandler.TwoArg<S, A1, A2> apiHandler)
            implements Handler<S, A1, A2, Void, Void> {

        @Override
        public void handleRequest(ApiRequest<S, A1, A2, Void, Void> apiRequest) throws Exception {
            apiHandler.handleRequest(apiRequest.sender, apiRequest.arg1, apiRequest.arg2, apiRequest.dispatcher);
        }
    }

    /** {@link Handler} that invokes an {@link ApiHandler.ThreeArg}. */
    private record ThreeArgHandler<S extends Sender, A1, A2, A3>(ApiHandler.ThreeArg<S, A1, A2, A3> apiHandler)
            implements Handler<S, A1, A2, A3, Void> {

        @Override
        public void handleRequest(ApiRequest<S, A1, A2, A3, Void> apiRequest) throws Exception {
            apiHandler.handleRequest(
                    apiRequest.sender, apiRequest.arg1, apiRequest.arg2, apiRequest.arg3, apiRequest.dispatcher);
        }
    }

    /** {@link Handler} that invokes an {@link ApiHandler.FourArg}. */
    private record FourAgeHandler<S extends Sender, A1, A2, A3, A4>(ApiHandler.FourArg<S, A1, A2, A3, A4> apiHandler)
            implements Handler<S, A1, A2, A3, A4> {

        @Override
        public void handleRequest(ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception {
            apiHandler.handleRequest(
                    apiRequest.sender,
                    apiRequest.arg1,
                    apiRequest.arg2,
                    apiRequest.arg3,
                    apiRequest.arg4,
                    apiRequest.dispatcher);
        }
    }
}
