package org.example.age.api.adapter;

import java.util.function.Consumer;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.HttpOptional;
import org.example.age.api.base.Sender;

final class AdaptedApiHandlerImpl<E, S extends Sender, A1, A2, A3, A4> implements AdaptedApiHandler<E> {

    private final ApiRequest.Factory<E, S> apiRequestFactory;
    private final Extractor.Async<E, A1> arg1Extractor;
    private final Extractor.Async<E, A2> arg2Extractor;
    private final Extractor.Async<E, A3> arg3Extractor;
    private final Extractor.Async<E, A4> arg4Extractor;
    private final ApiRequest.Handler<S, A1, A2, A3, A4> apiRequestHandler;

    /** Creates a builder for an {@link AdaptedApiHandler} with zero or more arguments. */
    public static <E, S extends Sender> ZeroArgBuilder<E, S> builder(
            SenderFactory<E, S> senderFactory, DispatcherFactory<E> dispatcherFactory) {
        ApiRequest.Factory<E, S> apiRequestFactory = ApiRequest.Factory.create(senderFactory, dispatcherFactory);
        return new ZeroArgBuilderImpl<>(apiRequestFactory);
    }

    @Override
    public void handleRequest(E exchange) throws Exception {
        ApiRequest<S, A1, A2, A3, A4> apiRequest = apiRequestFactory.create(exchange);
        extractNextArgOrHandleApiRequest(exchange, apiRequest, arg1Extractor, apiRequest::setArg1, this::onArg1Set);
    }

    /** Called when the first argument has been set. */
    private void onArg1Set(E exchange, ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception {
        extractNextArgOrHandleApiRequest(exchange, apiRequest, arg2Extractor, apiRequest::setArg2, this::onArg2Set);
    }

    /** Called when the second argument has been set. */
    private void onArg2Set(E exchange, ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception {
        extractNextArgOrHandleApiRequest(exchange, apiRequest, arg3Extractor, apiRequest::setArg3, this::onArg3Set);
    }

    /** Called when the third argument has been set. */
    private void onArg3Set(E exchange, ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception {
        extractNextArgOrHandleApiRequest(exchange, apiRequest, arg4Extractor, apiRequest::setArg4, this::onArg4Set);
    }

    /** Called when the fourth argument has been set. */
    private void onArg4Set(E exchange, ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception {
        apiRequestHandler.handleRequest(apiRequest);
    }

    /** Extracts the next argument, or handles the API request if all arguments have been extracted. */
    private <A> void extractNextArgOrHandleApiRequest(
            E exchange,
            ApiRequest<S, A1, A2, A3, A4> apiRequest,
            Extractor.Async<E, A> argExtractor,
            Consumer<A> setter,
            AdapterCallback<E, S, A1, A2, A3, A4> next)
            throws Exception {
        if (argExtractor == null) {
            apiRequestHandler.handleRequest(apiRequest);
            return;
        }

        ArgExtractorCallback<E, S, A1, A2, A3, A4, A> callback =
                new ArgExtractorCallback<>(apiRequestHandler, exchange, apiRequest, setter, next);
        argExtractor.tryExtract(exchange, callback);
    }

    private AdaptedApiHandlerImpl(
            ApiRequest.Factory<E, S> apiRequestFactory,
            Extractor.Async<E, A1> arg1Extractor,
            Extractor.Async<E, A2> arg2Extractor,
            Extractor.Async<E, A3> arg3Extractor,
            Extractor.Async<E, A4> arg4Extractor,
            ApiRequest.Handler<S, A1, A2, A3, A4> apiRequestHandler) {
        this.apiRequestFactory = apiRequestFactory;
        this.arg1Extractor = arg1Extractor;
        this.arg2Extractor = arg2Extractor;
        this.arg3Extractor = arg3Extractor;
        this.arg4Extractor = arg4Extractor;
        this.apiRequestHandler = apiRequestHandler;
    }

    /** Internal {@link ZeroArgBuilder} implementation. */
    private record ZeroArgBuilderImpl<E, S extends Sender>(ApiRequest.Factory<E, S> apiRequestFactory)
            implements AdaptedApiHandler.ZeroArgBuilder<E, S> {

        @Override
        public AdaptedApiHandler<E> build(ApiHandler.ZeroArg<S> apiHandler) {
            ApiRequest.Handler<S, Void, Void, Void, Void> apiRequestHandler = ApiRequest.Handler.zeroArg(apiHandler);
            return new AdaptedApiHandlerImpl<>(apiRequestFactory, null, null, null, null, apiRequestHandler);
        }

        @Override
        public <A1> OneArgBuilder<E, S, A1> addExtractor(Extractor.Async<E, A1> extractor) {
            return new OneArgBuilderImpl<>(this, extractor);
        }
    }

    /** Internal {@link OneArgBuilder} implementation. */
    private record OneArgBuilderImpl<E, S extends Sender, A1>(
            ZeroArgBuilderImpl<E, S> builder0, Extractor.Async<E, A1> extractor) implements OneArgBuilder<E, S, A1> {

        @Override
        public AdaptedApiHandler<E> build(ApiHandler.OneArg<S, A1> apiHandler) {
            ApiRequest.Handler<S, A1, Void, Void, Void> apiRequestHandler = ApiRequest.Handler.oneArg(apiHandler);
            return new AdaptedApiHandlerImpl<>(
                    builder0.apiRequestFactory, extractor, null, null, null, apiRequestHandler);
        }

        @Override
        public <A2> TwoArgBuilder<E, S, A1, A2> addExtractor(Extractor.Async<E, A2> extractor) {
            return new TwoArgBuilderImpl<>(this, extractor);
        }
    }

    /** Internal {@link TwoArgBuilder} implementation. */
    private record TwoArgBuilderImpl<E, S extends Sender, A1, A2>(
            OneArgBuilderImpl<E, S, A1> builder1, Extractor.Async<E, A2> extractor)
            implements TwoArgBuilder<E, S, A1, A2> {

        @Override
        public AdaptedApiHandler<E> build(ApiHandler.TwoArg<S, A1, A2> apiHandler) {
            ApiRequest.Handler<S, A1, A2, Void, Void> apiRequestHandler = ApiRequest.Handler.twoArg(apiHandler);
            ZeroArgBuilderImpl<E, S> builder0 = builder1.builder0;
            return new AdaptedApiHandlerImpl<>(
                    builder0.apiRequestFactory, builder1.extractor, extractor, null, null, apiRequestHandler);
        }

        @Override
        public <A3> ThreeArgBuilder<E, S, A1, A2, A3> addExtractor(Extractor.Async<E, A3> extractor) {
            return new ThreeArgBuilderImpl<>(this, extractor);
        }
    }

    /** Internal {@link ThreeArgBuilder} implementation. */
    private record ThreeArgBuilderImpl<E, S extends Sender, A1, A2, A3>(
            TwoArgBuilderImpl<E, S, A1, A2> builder2, Extractor.Async<E, A3> extractor)
            implements ThreeArgBuilder<E, S, A1, A2, A3> {

        @Override
        public AdaptedApiHandler<E> build(ApiHandler.ThreeArg<S, A1, A2, A3> apiHandler) {
            ApiRequest.Handler<S, A1, A2, A3, Void> apiRequestHandler = ApiRequest.Handler.threeArg(apiHandler);
            OneArgBuilderImpl<E, S, A1> builder1 = builder2.builder1;
            ZeroArgBuilderImpl<E, S> builder0 = builder1.builder0;
            return new AdaptedApiHandlerImpl<>(
                    builder0.apiRequestFactory,
                    builder1.extractor,
                    builder2.extractor,
                    extractor,
                    null,
                    apiRequestHandler);
        }

        @Override
        public <A4> FourArgBuilder<E, S, A1, A2, A3, A4> addExtractor(Extractor.Async<E, A4> extractor) {
            return new FourArgBuilderImpl<>(this, extractor);
        }
    }

    /** Internal {@link FourArgBuilder} implementation. */
    private record FourArgBuilderImpl<E, S extends Sender, A1, A2, A3, A4>(
            ThreeArgBuilderImpl<E, S, A1, A2, A3> builder3, Extractor.Async<E, A4> extractor)
            implements FourArgBuilder<E, S, A1, A2, A3, A4> {

        @Override
        public AdaptedApiHandler<E> build(ApiHandler.FourArg<S, A1, A2, A3, A4> apiHandler) {
            ApiRequest.Handler<S, A1, A2, A3, A4> apiRequestHandler = ApiRequest.Handler.fourArg(apiHandler);
            TwoArgBuilderImpl<E, S, A1, A2> builder2 = builder3.builder2;
            OneArgBuilderImpl<E, S, A1> builder1 = builder2.builder1;
            ZeroArgBuilderImpl<E, S> builder0 = builder1.builder0;
            return new AdaptedApiHandlerImpl<>(
                    builder0.apiRequestFactory,
                    builder1.extractor,
                    builder2.extractor,
                    builder3.extractor,
                    extractor,
                    apiRequestHandler);
        }
    }

    /** Callback used to handle the underlying exchange by adapting it to an {@link ApiRequest}. */
    @FunctionalInterface
    private interface AdapterCallback<E, S extends Sender, A1, A2, A3, A4> {

        void handleRequest(E exchange, ApiRequest<S, A1, A2, A3, A4> apiRequest) throws Exception;
    }

    /** Sets the argument that has been extracted, or sends an error status code if extraction fails. */
    private record ArgExtractorCallback<E, S extends Sender, A1, A2, A3, A4, A>(
            ApiRequest.Handler<S, A1, A2, A3, A4> apiRequestHandler,
            E exchange,
            ApiRequest<S, A1, A2, A3, A4> apiRequest,
            Consumer<A> setter,
            AdapterCallback<E, S, A1, A2, A3, A4> next)
            implements Extractor.Callback<A> {

        @Override
        public void onValueExtracted(HttpOptional<A> maybeArg) throws Exception {
            if (maybeArg.isEmpty()) {
                apiRequestHandler.sendErrorCode(apiRequest, maybeArg.statusCode());
                return;
            }
            A arg = maybeArg.get();

            setter.accept(arg);
            next.handleRequest(exchange, apiRequest);
        }
    }
}
