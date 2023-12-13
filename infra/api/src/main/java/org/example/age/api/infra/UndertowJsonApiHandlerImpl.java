package org.example.age.api.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.example.age.api.adapter.AdaptedApiHandler;
import org.example.age.api.adapter.Extractor;
import org.example.age.api.base.ApiHandler;
import org.example.age.api.base.Sender;

final class UndertowJsonApiHandlerImpl implements UndertowJsonApiHandler {

    private final AdaptedApiHandler<HttpServerExchange> handler;

    /** Creates a builder for an {@link HttpHandler} that only sends a status code. */
    static ZeroArgBuilder<Sender.StatusCode> builder() {
        return new ZeroArgBuilderImpl<>(
                AdaptedApiHandler.builder(UndertowSender.StatusCode::create, UndertowDispatcher::create));
    }

    /** Creates a builder for an {@link HttpHandler} that sends a JSON value (or an error status code). */
    static <V> ZeroArgBuilder<Sender.Value<V>> builder(TypeReference<V> responseValueTypeRef) {
        return new ZeroArgBuilderImpl<>(
                AdaptedApiHandler.builder(UndertowSender.JsonValue::create, UndertowDispatcher::create));
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        handler.handleRequest(exchange);
    }

    private UndertowJsonApiHandlerImpl(AdaptedApiHandler<HttpServerExchange> handler) {
        this.handler = handler;
    }

    /** Internal {@link ZeroArgBuilder} implementation. */
    private record ZeroArgBuilderImpl<S extends Sender>(
            AdaptedApiHandler.ZeroArgBuilder<HttpServerExchange, S> builder0) implements ZeroArgBuilder<S> {

        @Override
        public HttpHandler build(ApiHandler.ZeroArg<S> apiHandler) {
            return new UndertowJsonApiHandlerImpl(builder0.build(apiHandler));
        }

        @Override
        public <A1> OneArgBuilder<S, A1> addExtractor(Extractor.Async<HttpServerExchange, A1> extractor) {
            return new OneArgBuilderImpl<>(builder0.addExtractor(extractor));
        }
    }

    /** Internal {@link OneArgBuilder} implementation. */
    private record OneArgBuilderImpl<S extends Sender, A1>(
            AdaptedApiHandler.OneArgBuilder<HttpServerExchange, S, A1> builder1) implements OneArgBuilder<S, A1> {

        @Override
        public HttpHandler build(ApiHandler.OneArg<S, A1> apiHandler) {
            return new UndertowJsonApiHandlerImpl(builder1.build(apiHandler));
        }

        @Override
        public <A2> TwoArgBuilder<S, A1, A2> addExtractor(Extractor.Async<HttpServerExchange, A2> extractor) {
            return new TwoArgBuilderImpl<>(builder1.addExtractor(extractor));
        }
    }

    /** Internal {@link TwoArgBuilder} implementation. */
    private record TwoArgBuilderImpl<S extends Sender, A1, A2>(
            AdaptedApiHandler.TwoArgBuilder<HttpServerExchange, S, A1, A2> builder2)
            implements TwoArgBuilder<S, A1, A2> {

        @Override
        public HttpHandler build(ApiHandler.TwoArg<S, A1, A2> apiHandler) {
            return new UndertowJsonApiHandlerImpl(builder2.build(apiHandler));
        }

        @Override
        public <A3> ThreeArgBuilder<S, A1, A2, A3> addExtractor(Extractor.Async<HttpServerExchange, A3> extractor) {
            return new ThreeArgBuilderImpl<>(builder2.addExtractor(extractor));
        }
    }

    /** Internal {@link ThreeArgBuilder} implementation. */
    private record ThreeArgBuilderImpl<S extends Sender, A1, A2, A3>(
            AdaptedApiHandler.ThreeArgBuilder<HttpServerExchange, S, A1, A2, A3> builder3)
            implements ThreeArgBuilder<S, A1, A2, A3> {

        @Override
        public HttpHandler build(ApiHandler.ThreeArg<S, A1, A2, A3> apiHandler) {
            return new UndertowJsonApiHandlerImpl(builder3.build(apiHandler));
        }

        @Override
        public <A4> FourArgBuilder<S, A1, A2, A3, A4> addExtractor(Extractor.Async<HttpServerExchange, A4> extractor) {
            return new FourArgBuilderImpl<>(builder3.addExtractor(extractor));
        }
    }

    /** Internal {@link FourArgBuilder} implementation. */
    private record FourArgBuilderImpl<S extends Sender, A1, A2, A3, A4>(
            AdaptedApiHandler.FourArgBuilder<HttpServerExchange, S, A1, A2, A3, A4> builder4)
            implements FourArgBuilder<S, A1, A2, A3, A4> {

        @Override
        public HttpHandler build(ApiHandler.FourArg<S, A1, A2, A3, A4> apiHandler) {
            return new UndertowJsonApiHandlerImpl(builder4.build(apiHandler));
        }
    }
}
