package org.example.age.api.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.example.age.api.adapter.Extractor;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Sender;

/** {@link HttpHandler} that invokes an {@link ApiHandler}. */
public interface UndertowJsonApiHandler extends HttpHandler {

    /** Creates a builder for an {@link HttpHandler} that only sends a status code. */
    static ZeroArgBuilder<Sender.StatusCode> builder() {
        return UndertowJsonApiHandlerImpl.builder();
    }

    /** Creates a builder for an {@link HttpHandler} that sends a JSON value (or an error status code). */
    static <V> ZeroArgBuilder<Sender.Value<V>> builder(TypeReference<V> responseValueTypeRef) {
        return UndertowJsonApiHandlerImpl.builder(responseValueTypeRef);
    }

    /** Creates an {@link HttpHandler} that sends a 404 error. */
    static HttpHandler notFound() {
        return exchange -> UndertowResponses.sendStatusCode(exchange, StatusCodes.NOT_FOUND);
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} with zero or more arguments. */
    interface ZeroArgBuilder<S extends Sender> extends ArgBuilder<ApiHandler.ZeroArg<S>> {

        @Override
        default <A1> OneArgBuilder<S, A1> addBody(TypeReference<A1> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.body(valueTypeRef));
        }

        @Override
        default OneArgBuilder<S, String> addQueryParam(String name) {
            return addExtractor(UndertowJsonExtractors.queryParam(name));
        }

        @Override
        default <A1> OneArgBuilder<S, A1> addQueryParam(String name, TypeReference<A1> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.queryParam(name, valueTypeRef));
        }

        @Override
        default <A1> OneArgBuilder<S, A1> addExtractor(Extractor<HttpServerExchange, A1> extractor) {
            return addExtractor(extractor.async());
        }

        @Override
        <A1> OneArgBuilder<S, A1> addExtractor(Extractor.Async<HttpServerExchange, A1> extractor);
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} with one or more arguments. */
    interface OneArgBuilder<S extends Sender, A1> extends ArgBuilder<ApiHandler.OneArg<S, A1>> {

        @Override
        default <A2> TwoArgBuilder<S, A1, A2> addBody(TypeReference<A2> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.body(valueTypeRef));
        }

        @Override
        default TwoArgBuilder<S, A1, String> addQueryParam(String name) {
            return addExtractor(UndertowJsonExtractors.queryParam(name));
        }

        @Override
        default <A2> TwoArgBuilder<S, A1, A2> addQueryParam(String name, TypeReference<A2> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.queryParam(name, valueTypeRef));
        }

        @Override
        default <A2> TwoArgBuilder<S, A1, A2> addExtractor(Extractor<HttpServerExchange, A2> extractor) {
            return addExtractor(extractor.async());
        }

        @Override
        <A2> TwoArgBuilder<S, A1, A2> addExtractor(Extractor.Async<HttpServerExchange, A2> extractor);
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} with two or more arguments. */
    interface TwoArgBuilder<S extends Sender, A1, A2> extends ArgBuilder<ApiHandler.TwoArg<S, A1, A2>> {

        @Override
        default <A3> ThreeArgBuilder<S, A1, A2, A3> addBody(TypeReference<A3> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.body(valueTypeRef));
        }

        @Override
        default ThreeArgBuilder<S, A1, A2, String> addQueryParam(String name) {
            return addExtractor(UndertowJsonExtractors.queryParam(name));
        }

        @Override
        default <A3> ThreeArgBuilder<S, A1, A2, A3> addQueryParam(String name, TypeReference<A3> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.queryParam(name, valueTypeRef));
        }

        @Override
        default <A3> ThreeArgBuilder<S, A1, A2, A3> addExtractor(Extractor<HttpServerExchange, A3> extractor) {
            return addExtractor(extractor.async());
        }

        @Override
        <A3> ThreeArgBuilder<S, A1, A2, A3> addExtractor(Extractor.Async<HttpServerExchange, A3> extractor);
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} with three or more arguments. */
    interface ThreeArgBuilder<S extends Sender, A1, A2, A3> extends ArgBuilder<ApiHandler.ThreeArg<S, A1, A2, A3>> {

        @Override
        default <A4> FourArgBuilder<S, A1, A2, A3, A4> addBody(TypeReference<A4> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.body(valueTypeRef));
        }

        @Override
        default FourArgBuilder<S, A1, A2, A3, String> addQueryParam(String name) {
            return addExtractor(UndertowJsonExtractors.queryParam(name));
        }

        @Override
        default <A4> FourArgBuilder<S, A1, A2, A3, A4> addQueryParam(String name, TypeReference<A4> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.queryParam(name, valueTypeRef));
        }

        @Override
        default <A4> FourArgBuilder<S, A1, A2, A3, A4> addExtractor(Extractor<HttpServerExchange, A4> extractor) {
            return addExtractor(extractor.async());
        }

        @Override
        <A4> FourArgBuilder<S, A1, A2, A3, A4> addExtractor(Extractor.Async<HttpServerExchange, A4> extractor);
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} with four arguments. */
    interface FourArgBuilder<S extends Sender, A1, A2, A3, A4> extends Builder<ApiHandler.FourArg<S, A1, A2, A3, A4>> {}

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler}. */
    interface Builder<H> {

        /** Builds an {@link HttpHandler} that invokes an {@link ApiHandler}. */
        HttpHandler build(H apiHandler);
    }

    /**
     * Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} that can add another argument.
     *
     * <p>Sub-interfaces will override the return type.</p>
     */
    interface ArgBuilder<H> extends Builder<H> {

        /** Adds an argument from the request body. */
        <A> Object addBody(TypeReference<A> valueTypeRef);

        /** Adds an argument from the (first) value of a query parameter. */
        Object addQueryParam(String name);

        /** Adds an argument from the (first) value of a query parameter. */
        <A> Object addQueryParam(String name, TypeReference<A> valueTypeRef);

        /** Adds an argument that is extracted from the {@link HttpServerExchange}. */
        <A> Object addExtractor(Extractor<HttpServerExchange, A> extractor);

        /** Adds an argument that is extracted from the {@link HttpServerExchange}. */
        <A> Object addExtractor(Extractor.Async<HttpServerExchange, A> extractor);
    }
}
