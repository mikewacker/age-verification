package org.example.age.api.adapter;

import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Sender;

/**
 * Adapts an {@link ApiHandler} to a handler for the underlying server.
 *
 * <p>In practice, consumers will create a specialized builder that is backed by {@link ApiHandler}'s builder.</p>
 */
@FunctionalInterface
public interface AdaptedApiHandler<E> {

    /** Creates a builder for an {@link AdaptedApiHandler} with zero or more arguments. */
    static <E, S extends Sender> ZeroArgBuilder<E, S> builder(
            SenderFactory<E, S> senderFactory, DispatcherFactory<E> dispatcherFactory) {
        return AdaptedApiHandlerImpl.builder(senderFactory, dispatcherFactory);
    }

    void handleRequest(E exchange) throws Exception;

    /** Builder for an {@link AdaptedApiHandler} with zero or more arguments. */
    interface ZeroArgBuilder<E, S extends Sender> extends ArgBuilder<E, ApiHandler.ZeroArg<S>> {

        @Override
        <A1> OneArgBuilder<E, S, A1> addExtractor(Extractor.Async<E, A1> extractor);

        @Override
        default <A1> OneArgBuilder<E, S, A1> addExtractor(Extractor<E, A1> extractor) {
            return addExtractor(extractor.async());
        }
    }

    /** Builder for an {@link AdaptedApiHandler} with one or more arguments. */
    interface OneArgBuilder<E, S extends Sender, A1> extends ArgBuilder<E, ApiHandler.OneArg<S, A1>> {

        @Override
        <A2> TwoArgBuilder<E, S, A1, A2> addExtractor(Extractor.Async<E, A2> extractor);

        @Override
        default <A2> TwoArgBuilder<E, S, A1, A2> addExtractor(Extractor<E, A2> extractor) {
            return addExtractor(extractor.async());
        }
    }

    /** Builder for an {@link AdaptedApiHandler} with two or more arguments. */
    interface TwoArgBuilder<E, S extends Sender, A1, A2> extends ArgBuilder<E, ApiHandler.TwoArg<S, A1, A2>> {

        @Override
        <A3> ThreeArgBuilder<E, S, A1, A2, A3> addExtractor(Extractor.Async<E, A3> extractor);

        @Override
        default <A3> ThreeArgBuilder<E, S, A1, A2, A3> addExtractor(Extractor<E, A3> extractor) {
            return addExtractor(extractor.async());
        }
    }

    /** Builder for an {@link AdaptedApiHandler} with three or more arguments. */
    interface ThreeArgBuilder<E, S extends Sender, A1, A2, A3>
            extends ArgBuilder<E, ApiHandler.ThreeArg<S, A1, A2, A3>> {

        @Override
        <A4> FourArgBuilder<E, S, A1, A2, A3, A4> addExtractor(Extractor.Async<E, A4> extractor);

        @Override
        default <A4> FourArgBuilder<E, S, A1, A2, A3, A4> addExtractor(Extractor<E, A4> extractor) {
            return addExtractor(extractor.async());
        }
    }

    /** Builder for an {@link AdaptedApiHandler} with four arguments. */
    interface FourArgBuilder<E, S extends Sender, A1, A2, A3, A4>
            extends Builder<E, ApiHandler.FourArg<S, A1, A2, A3, A4>> {}

    /** Builder for an {@link AdaptedApiHandler}. */
    interface Builder<E, H> {

        /** Builds an {@link AdaptedApiHandler} that invokes an {@link ApiHandler}. */
        AdaptedApiHandler<E> build(H apiHandler);
    }

    /**
     * Builder for an {@link AdaptedApiHandler} that can add another argument.
     *
     * <p>Sub-interfaces will override the return type.</p>
     */
    interface ArgBuilder<E, H> extends Builder<E, H> {

        /** Adds an argument that is extracted from the underlying request. */
        <A> Object addExtractor(Extractor.Async<E, A> extractor);

        /** Adds an argument that is extracted from the underlying request. */
        <A> Object addExtractor(Extractor<E, A> extractor);
    }
}
