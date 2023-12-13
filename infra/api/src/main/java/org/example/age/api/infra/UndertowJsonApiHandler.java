package org.example.age.api.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.StatusCodes;
import org.example.age.api.adapter.AdaptedApiHandler;
import org.example.age.api.adapter.Extractor;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Sender;

/** {@link HttpHandler} that invokes an {@link ApiHandler}. */
public final class UndertowJsonApiHandler implements HttpHandler {

    private final AdaptedApiHandler<HttpServerExchange> handler;

    /** Creates a builder for an {@link HttpHandler} that only sends a status code. */
    public static ZeroArgBuilder<Sender.StatusCode> builder() {
        AdaptedApiHandler.ZeroArgBuilder<HttpServerExchange, Sender.StatusCode> builder0 =
                AdaptedApiHandler.builder(UndertowSender.StatusCode::create, UndertowDispatcher::create);
        return new ZeroArgBuilder<>(builder0);
    }

    /** Creates a builder for an {@link HttpHandler} that sends a JSON value (or an error status code). */
    public static <V> ZeroArgBuilder<Sender.Value<V>> builder(TypeReference<V> responseValueTypeRef) {
        AdaptedApiHandler.ZeroArgBuilder<HttpServerExchange, Sender.Value<V>> builder0 =
                AdaptedApiHandler.builder(UndertowSender.JsonValue::create, UndertowDispatcher::create);
        return new ZeroArgBuilder<>(builder0);
    }

    /** Creates an {@link HttpHandler} that sends a 404 error. */
    public static HttpHandler notFound() {
        return exchange -> UndertowResponse.sendStatusCode(exchange, StatusCodes.NOT_FOUND);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        handler.handleRequest(exchange);
    }

    private UndertowJsonApiHandler(AdaptedApiHandler<HttpServerExchange> handler) {
        this.handler = handler;
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} with zero or more arguments. */
    public static final class ZeroArgBuilder<S extends Sender> implements ArgBuilder<ApiHandler.ZeroArg<S>> {

        private final AdaptedApiHandler.ZeroArgBuilder<HttpServerExchange, S> builder0;

        @Override
        public HttpHandler build(ApiHandler.ZeroArg<S> apiHandler) {
            AdaptedApiHandler<HttpServerExchange> handler = builder0.build(apiHandler);
            return new UndertowJsonApiHandler(handler);
        }

        @Override
        public <A1> OneArgBuilder<S, A1> addBody(TypeReference<A1> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.body(valueTypeRef));
        }

        @Override
        public OneArgBuilder<S, String> addQueryParam(String name) {
            return addExtractor(UndertowJsonExtractors.queryParam(name));
        }

        @Override
        public <A1> OneArgBuilder<S, A1> addQueryParam(String name, TypeReference<A1> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.queryParam(name, valueTypeRef));
        }

        @Override
        public <A1> OneArgBuilder<S, A1> addExtractor(Extractor<HttpServerExchange, A1> extractor) {
            return addExtractor(extractor.async());
        }

        @Override
        public <A1> OneArgBuilder<S, A1> addExtractor(Extractor.Async<HttpServerExchange, A1> extractor) {
            AdaptedApiHandler.OneArgBuilder<HttpServerExchange, S, A1> builder1 = builder0.addExtractor(extractor);
            return new OneArgBuilder<>(builder1);
        }

        private ZeroArgBuilder(AdaptedApiHandler.ZeroArgBuilder<HttpServerExchange, S> builder0) {
            this.builder0 = builder0;
        }
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} with one or more arguments. */
    public static final class OneArgBuilder<S extends Sender, A1> implements ArgBuilder<ApiHandler.OneArg<S, A1>> {

        private final AdaptedApiHandler.OneArgBuilder<HttpServerExchange, S, A1> builder1;

        @Override
        public HttpHandler build(ApiHandler.OneArg<S, A1> apiHandler) {
            AdaptedApiHandler<HttpServerExchange> handler = builder1.build(apiHandler);
            return new UndertowJsonApiHandler(handler);
        }

        @Override
        public <A2> TwoArgBuilder<S, A1, A2> addBody(TypeReference<A2> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.body(valueTypeRef));
        }

        @Override
        public TwoArgBuilder<S, A1, String> addQueryParam(String name) {
            return addExtractor(UndertowJsonExtractors.queryParam(name));
        }

        @Override
        public <A2> TwoArgBuilder<S, A1, A2> addQueryParam(String name, TypeReference<A2> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.queryParam(name, valueTypeRef));
        }

        @Override
        public <A2> TwoArgBuilder<S, A1, A2> addExtractor(Extractor<HttpServerExchange, A2> extractor) {
            return addExtractor(extractor.async());
        }

        @Override
        public <A2> TwoArgBuilder<S, A1, A2> addExtractor(Extractor.Async<HttpServerExchange, A2> extractor) {
            AdaptedApiHandler.TwoArgBuilder<HttpServerExchange, S, A1, A2> builder2 = builder1.addExtractor(extractor);
            return new TwoArgBuilder<>(builder2);
        }

        private OneArgBuilder(AdaptedApiHandler.OneArgBuilder<HttpServerExchange, S, A1> builder1) {
            this.builder1 = builder1;
        }
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} with two or more arguments. */
    public static final class TwoArgBuilder<S extends Sender, A1, A2>
            implements ArgBuilder<ApiHandler.TwoArg<S, A1, A2>> {

        private final AdaptedApiHandler.TwoArgBuilder<HttpServerExchange, S, A1, A2> builder2;

        @Override
        public HttpHandler build(ApiHandler.TwoArg<S, A1, A2> apiHandler) {
            AdaptedApiHandler<HttpServerExchange> handler = builder2.build(apiHandler);
            return new UndertowJsonApiHandler(handler);
        }

        @Override
        public <A3> ThreeArgBuilder<S, A1, A2, A3> addBody(TypeReference<A3> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.body(valueTypeRef));
        }

        @Override
        public ThreeArgBuilder<S, A1, A2, String> addQueryParam(String name) {
            return addExtractor(UndertowJsonExtractors.queryParam(name));
        }

        @Override
        public <A3> ThreeArgBuilder<S, A1, A2, A3> addQueryParam(String name, TypeReference<A3> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.queryParam(name, valueTypeRef));
        }

        @Override
        public <A3> ThreeArgBuilder<S, A1, A2, A3> addExtractor(Extractor<HttpServerExchange, A3> extractor) {
            return addExtractor(extractor.async());
        }

        @Override
        public <A3> ThreeArgBuilder<S, A1, A2, A3> addExtractor(Extractor.Async<HttpServerExchange, A3> extractor) {
            AdaptedApiHandler.ThreeArgBuilder<HttpServerExchange, S, A1, A2, A3> builder3 =
                    builder2.addExtractor(extractor);
            return new ThreeArgBuilder<>(builder3);
        }

        private TwoArgBuilder(AdaptedApiHandler.TwoArgBuilder<HttpServerExchange, S, A1, A2> builder2) {
            this.builder2 = builder2;
        }
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} with three or more arguments. */
    public static final class ThreeArgBuilder<S extends Sender, A1, A2, A3>
            implements ArgBuilder<ApiHandler.ThreeArg<S, A1, A2, A3>> {

        private final AdaptedApiHandler.ThreeArgBuilder<HttpServerExchange, S, A1, A2, A3> builder3;

        @Override
        public HttpHandler build(ApiHandler.ThreeArg<S, A1, A2, A3> apiHandler) {
            AdaptedApiHandler<HttpServerExchange> handler = builder3.build(apiHandler);
            return new UndertowJsonApiHandler(handler);
        }

        @Override
        public <A4> FourArgBuilder<S, A1, A2, A3, A4> addBody(TypeReference<A4> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.body(valueTypeRef));
        }

        @Override
        public FourArgBuilder<S, A1, A2, A3, String> addQueryParam(String name) {
            return addExtractor(UndertowJsonExtractors.queryParam(name));
        }

        @Override
        public <A4> FourArgBuilder<S, A1, A2, A3, A4> addQueryParam(String name, TypeReference<A4> valueTypeRef) {
            return addExtractor(UndertowJsonExtractors.queryParam(name, valueTypeRef));
        }

        @Override
        public <A4> FourArgBuilder<S, A1, A2, A3, A4> addExtractor(Extractor<HttpServerExchange, A4> extractor) {
            return addExtractor(extractor.async());
        }

        @Override
        public <A4> FourArgBuilder<S, A1, A2, A3, A4> addExtractor(Extractor.Async<HttpServerExchange, A4> extractor) {
            AdaptedApiHandler.FourArgBuilder<HttpServerExchange, S, A1, A2, A3, A4> builder4 =
                    builder3.addExtractor(extractor);
            return new FourArgBuilder<>(builder4);
        }

        private ThreeArgBuilder(AdaptedApiHandler.ThreeArgBuilder<HttpServerExchange, S, A1, A2, A3> builder3) {
            this.builder3 = builder3;
        }
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} with four arguments. */
    public static final class FourArgBuilder<S extends Sender, A1, A2, A3, A4>
            implements Builder<ApiHandler.FourArg<S, A1, A2, A3, A4>> {

        private final AdaptedApiHandler.FourArgBuilder<HttpServerExchange, S, A1, A2, A3, A4> builder4;

        @Override
        public HttpHandler build(ApiHandler.FourArg<S, A1, A2, A3, A4> apiHandler) {
            AdaptedApiHandler<HttpServerExchange> handler = builder4.build(apiHandler);
            return new UndertowJsonApiHandler(handler);
        }

        private FourArgBuilder(AdaptedApiHandler.FourArgBuilder<HttpServerExchange, S, A1, A2, A3, A4> builder4) {
            this.builder4 = builder4;
        }
    }

    /** Builder for an {@link HttpHandler} that invokes an {@link ApiHandler}. */
    private interface Builder<H> {

        /** Builds an {@link HttpHandler} that invokes an {@link ApiHandler}. */
        HttpHandler build(H apiHandler);
    }

    /**
     * Builder for an {@link HttpHandler} that invokes an {@link ApiHandler} that can add another argument.
     *
     * <p>Implementations will override the return type.</p>
     */
    private interface ArgBuilder<H> extends Builder<H> {

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
