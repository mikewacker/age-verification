package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.Sender;
import io.undertow.server.HttpHandler;
import org.example.age.api.infra.UndertowApiRouter;
import org.example.age.api.infra.UndertowJsonApiHandler;

/** Asynchronous API that proxies responses. It supports both status codes and text. */
public interface ProxyApi {

    /** Creates an {@link HttpHandler} from a {@link ProxyApi}. */
    static HttpHandler createHandler(ProxyApi api) {
        HttpHandler statusCodeHandler = UndertowJsonApiHandler.builder().build(api::proxyStatusCode);
        HttpHandler textHandler =
                UndertowJsonApiHandler.builder(new TypeReference<String>() {}).build(api::proxyText);

        return UndertowApiRouter.builder()
                .addRoute("/status-code", statusCodeHandler)
                .addRoute("/text", textHandler)
                .build();
    }

    /** Proxies a status code that is received. */
    void proxyStatusCode(Sender.StatusCode sender, Dispatcher dispatcher);

    /** Proxies text that is received. */
    void proxyText(Sender.Value<String> sender, Dispatcher dispatcher);
}
