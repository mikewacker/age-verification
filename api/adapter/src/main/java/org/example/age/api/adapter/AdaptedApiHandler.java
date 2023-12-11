package org.example.age.api.adapter;

import java.util.List;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Sender;

/**
 * Adapts an {@link ApiHandler} to a handler for the underlying server.
 *
 * <p>In practice, consumers will create a specialized builder that is backed by {@link ApiHandler}'s builder.</p>
 */
public abstract class AdaptedApiHandler<E> {

    /** Creates a builder for an {@link AdaptedApiHandler}. */
    public static <E, S extends Sender> ZeroArgBuilder<E, S> builder(
            SenderFactory<E, S> senderFactory, DispatcherFactory<E> dispatcherFactory) {
        return new ZeroArgBuilder<>(senderFactory, dispatcherFactory);
    }

    public abstract void handleRequest(E exchange) throws Exception;

    /** Handles the underlying request using the callback. */
    protected static <E, S extends Sender, A1, A2, A3, A4> void handleRequest(
            E exchange, AdapterCallback<E, S, A1, A2, A3, A4> callback) throws Exception {
        ApiRequest<S, A1, A2, A3, A4> apiRequest = new ApiRequest<>();
        callback.handleRequest(exchange, apiRequest);
    }

    private AdaptedApiHandler() {}

    /** Builder for an {@link AdaptedApiHandler} with zero or more arguments. */
    public static final class ZeroArgBuilder<E, S extends Sender> implements ArgBuilder<E, ApiHandler.ZeroArg<S>> {

        private final SenderFactory<E, S> senderFactory;
        private final DispatcherFactory<E> dispatcherFactory;

        @Override
        public AdaptedApiHandler<E> build(ApiHandler.ZeroArg<S> apiHandler) {
            return new ZeroArg<>(apiHandler, senderFactory, dispatcherFactory);
        }

        @Override
        public <A1> OneArgBuilder<E, S, A1> addExtractor(Extractor.Async<E, A1> extractor) {
            return new OneArgBuilder<>(this, extractor);
        }

        @Override
        public <A1> OneArgBuilder<E, S, A1> addExtractor(Extractor<E, A1> extractor) {
            return addExtractor(extractor.async());
        }

        private ZeroArgBuilder(SenderFactory<E, S> senderFactory, DispatcherFactory<E> dispatcherFactory) {
            this.senderFactory = senderFactory;
            this.dispatcherFactory = dispatcherFactory;
        }
    }

    /** Builder for an {@link AdaptedApiHandler} with one or more arguments. */
    public static final class OneArgBuilder<E, S extends Sender, A1>
            implements ArgBuilder<E, ApiHandler.OneArg<S, A1>> {

        private final ZeroArgBuilder<E, S> builder0;
        private final Extractor.Async<E, A1> extractor;

        @Override
        public AdaptedApiHandler<E> build(ApiHandler.OneArg<S, A1> apiHandler) {
            return new OneArg<>(apiHandler, builder0.senderFactory, builder0.dispatcherFactory, extractor);
        }

        @Override
        public <A2> TwoArgBuilder<E, S, A1, A2> addExtractor(Extractor.Async<E, A2> extractor) {
            return new TwoArgBuilder<>(this, extractor);
        }

        @Override
        public <A2> TwoArgBuilder<E, S, A1, A2> addExtractor(Extractor<E, A2> extractor) {
            return addExtractor(extractor.async());
        }

        private OneArgBuilder(ZeroArgBuilder<E, S> builder0, Extractor.Async<E, A1> extractor) {
            this.builder0 = builder0;
            this.extractor = extractor;
        }
    }

    /** Builder for an {@link AdaptedApiHandler} with two or more arguments. */
    public static final class TwoArgBuilder<E, S extends Sender, A1, A2>
            implements ArgBuilder<E, ApiHandler.TwoArg<S, A1, A2>> {

        private final OneArgBuilder<E, S, A1> builder1;
        private final Extractor.Async<E, A2> extractor;

        @Override
        public AdaptedApiHandler<E> build(ApiHandler.TwoArg<S, A1, A2> apiHandler) {
            ZeroArgBuilder<E, S> builder0 = builder1.builder0;
            return new TwoArg<>(
                    apiHandler, builder0.senderFactory, builder0.dispatcherFactory, builder1.extractor, extractor);
        }

        @Override
        public <A3> ThreeArgBuilder<E, S, A1, A2, A3> addExtractor(Extractor.Async<E, A3> extractor) {
            return new ThreeArgBuilder<>(this, extractor);
        }

        @Override
        public <A3> ThreeArgBuilder<E, S, A1, A2, A3> addExtractor(Extractor<E, A3> extractor) {
            return addExtractor(extractor.async());
        }

        private TwoArgBuilder(OneArgBuilder<E, S, A1> builder1, Extractor.Async<E, A2> extractor) {
            this.builder1 = builder1;
            this.extractor = extractor;
        }
    }

    /** Builder for an {@link AdaptedApiHandler} with three or more arguments. */
    public static final class ThreeArgBuilder<E, S extends Sender, A1, A2, A3>
            implements ArgBuilder<E, ApiHandler.ThreeArg<S, A1, A2, A3>> {

        private final TwoArgBuilder<E, S, A1, A2> builder2;
        private final Extractor.Async<E, A3> extractor;

        @Override
        public AdaptedApiHandler<E> build(ApiHandler.ThreeArg<S, A1, A2, A3> apiHandler) {
            OneArgBuilder<E, S, A1> builder1 = builder2.builder1;
            ZeroArgBuilder<E, S> builder0 = builder1.builder0;
            return new ThreeArg<>(
                    apiHandler,
                    builder0.senderFactory,
                    builder0.dispatcherFactory,
                    builder1.extractor,
                    builder2.extractor,
                    extractor);
        }

        @Override
        public <A4> FourArgBuilder<E, S, A1, A2, A3, A4> addExtractor(Extractor.Async<E, A4> extractor) {
            return new FourArgBuilder<>(this, extractor);
        }

        @Override
        public <A4> FourArgBuilder<E, S, A1, A2, A3, A4> addExtractor(Extractor<E, A4> extractor) {
            return addExtractor(extractor.async());
        }

        private ThreeArgBuilder(TwoArgBuilder<E, S, A1, A2> builder2, Extractor.Async<E, A3> extractor) {
            this.builder2 = builder2;
            this.extractor = extractor;
        }
    }

    /** Builder for an {@link AdaptedApiHandler} with four arguments. */
    public static final class FourArgBuilder<E, S extends Sender, A1, A2, A3, A4>
            implements Builder<E, ApiHandler.FourArg<S, A1, A2, A3, A4>> {

        private final ThreeArgBuilder<E, S, A1, A2, A3> builder3;
        private final Extractor.Async<E, A4> extractor;

        @Override
        public AdaptedApiHandler<E> build(ApiHandler.FourArg<S, A1, A2, A3, A4> apiHandler) {
            TwoArgBuilder<E, S, A1, A2> builder2 = builder3.builder2;
            OneArgBuilder<E, S, A1> builder1 = builder2.builder1;
            ZeroArgBuilder<E, S> builder0 = builder1.builder0;
            return new FourArg<>(
                    apiHandler,
                    builder0.senderFactory,
                    builder0.dispatcherFactory,
                    builder1.extractor,
                    builder2.extractor,
                    builder3.extractor,
                    extractor);
        }

        private FourArgBuilder(ThreeArgBuilder<E, S, A1, A2, A3> builder3, Extractor.Async<E, A4> extractor) {
            this.builder3 = builder3;
            this.extractor = extractor;
        }
    }

    /** Adapts an {@link ApiHandler.ZeroArg} to a handler for the underlying server. */
    public static final class ZeroArg<E, S extends Sender> extends AdaptedApiHandler<E> {

        private final AdapterCallback<E, S, Void, Void, Void, Void> callback;

        @Override
        public void handleRequest(E exchange) throws Exception {
            handleRequest(exchange, callback);
        }

        private ZeroArg(
                ApiHandler.ZeroArg<S> apiHandler,
                SenderFactory<E, S> senderFactory,
                DispatcherFactory<E> dispatcherFactory) {
            callback = AdapterCallbacks.chain(
                    AdapterCallbacks.zeroArgInvoker(apiHandler),
                    List.of(AdapterCallbacks.init(senderFactory, dispatcherFactory)));
        }
    }

    /** Adapts an {@link ApiHandler.OneArg} to a handler for the underlying server. */
    public static final class OneArg<E, S extends Sender, A> extends AdaptedApiHandler<E> {

        private final AdapterCallback<E, S, A, Void, Void, Void> callback;

        @Override
        public void handleRequest(E exchange) throws Exception {
            handleRequest(exchange, callback);
        }

        private OneArg(
                ApiHandler.OneArg<S, A> apiHandler,
                SenderFactory<E, S> senderFactory,
                DispatcherFactory<E> dispatcherFactory,
                Extractor.Async<E, A> argExtractor) {
            callback = AdapterCallbacks.chain(
                    AdapterCallbacks.oneArgInvoker(apiHandler),
                    List.of(
                            AdapterCallbacks.init(senderFactory, dispatcherFactory),
                            AdapterCallbacks.arg1(argExtractor)));
        }
    }

    /** Adapts an {@link ApiHandler.TwoArg} to a handler for the underlying server. */
    public static final class TwoArg<E, S extends Sender, A1, A2> extends AdaptedApiHandler<E> {

        private final AdapterCallback<E, S, A1, A2, Void, Void> callback;

        @Override
        public void handleRequest(E exchange) throws Exception {
            handleRequest(exchange, callback);
        }

        private TwoArg(
                ApiHandler.TwoArg<S, A1, A2> apiHandler,
                SenderFactory<E, S> senderFactory,
                DispatcherFactory<E> dispatcherFactory,
                Extractor.Async<E, A1> arg1Extractor,
                Extractor.Async<E, A2> arg2Extractor) {
            callback = AdapterCallbacks.chain(
                    AdapterCallbacks.twoArgInvoker(apiHandler),
                    List.of(
                            AdapterCallbacks.init(senderFactory, dispatcherFactory),
                            AdapterCallbacks.arg1(arg1Extractor),
                            AdapterCallbacks.arg2(arg2Extractor)));
        }
    }

    /** Adapts an {@link ApiHandler.ThreeArg} to a handler for the underlying server. */
    public static final class ThreeArg<E, S extends Sender, A1, A2, A3> extends AdaptedApiHandler<E> {

        private final AdapterCallback<E, S, A1, A2, A3, Void> callback;

        @Override
        public void handleRequest(E exchange) throws Exception {
            handleRequest(exchange, callback);
        }

        private ThreeArg(
                ApiHandler.ThreeArg<S, A1, A2, A3> apiHandler,
                SenderFactory<E, S> senderFactory,
                DispatcherFactory<E> dispatcherFactory,
                Extractor.Async<E, A1> arg1Extractor,
                Extractor.Async<E, A2> arg2Extractor,
                Extractor.Async<E, A3> arg3Extractor) {
            callback = AdapterCallbacks.chain(
                    AdapterCallbacks.threeArgInvoker(apiHandler),
                    List.of(
                            AdapterCallbacks.init(senderFactory, dispatcherFactory),
                            AdapterCallbacks.arg1(arg1Extractor),
                            AdapterCallbacks.arg2(arg2Extractor),
                            AdapterCallbacks.arg3(arg3Extractor)));
        }
    }

    /** Adapts an {@link ApiHandler.FourArg} to a handler for the underlying server. */
    public static final class FourArg<E, S extends Sender, A1, A2, A3, A4> extends AdaptedApiHandler<E> {

        private final AdapterCallback<E, S, A1, A2, A3, A4> callback;

        @Override
        public void handleRequest(E exchange) throws Exception {
            handleRequest(exchange, callback);
        }

        private FourArg(
                ApiHandler.FourArg<S, A1, A2, A3, A4> apiHandler,
                SenderFactory<E, S> senderFactory,
                DispatcherFactory<E> dispatcherFactory,
                Extractor.Async<E, A1> arg1Extractor,
                Extractor.Async<E, A2> arg2Extractor,
                Extractor.Async<E, A3> arg3Extractor,
                Extractor.Async<E, A4> arg4Extractor) {
            callback = AdapterCallbacks.chain(
                    AdapterCallbacks.fourArgInvoker(apiHandler),
                    List.of(
                            AdapterCallbacks.init(senderFactory, dispatcherFactory),
                            AdapterCallbacks.arg1(arg1Extractor),
                            AdapterCallbacks.arg2(arg2Extractor),
                            AdapterCallbacks.arg3(arg3Extractor),
                            AdapterCallbacks.arg4(arg4Extractor)));
        }
    }

    /** Builder for an {@link AdaptedApiHandler}. */
    private interface Builder<E, H> {

        /** Builds an {@link AdaptedApiHandler} that invokes an {@link ApiHandler}. */
        AdaptedApiHandler<E> build(H apiHandler);
    }

    /**
     * Builder for an {@link AdaptedApiHandler} that can add another argument.
     *
     * <p>Implementations will override the return type.</p>
     */
    private interface ArgBuilder<E, H> extends Builder<E, H> {

        /** Adds an argument that is extracted from the underlying request. */
        <A> Object addExtractor(Extractor.Async<E, A> extractor);

        /** Adds an argument that is extracted from the underlying request. */
        <A> Object addExtractor(Extractor<E, A> extractor);
    }
}
