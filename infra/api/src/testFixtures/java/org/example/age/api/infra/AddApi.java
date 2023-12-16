package org.example.age.api.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import io.undertow.server.HttpHandler;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;

/** Asynchronous API that adds two numbers. Also contains a health check. */
public interface AddApi {

    /** Creates an {@link HttpHandler} from an {@link AddApi}. */
    static HttpHandler createHandler(AddApi api) {
        HttpHandler addHandler = UndertowJsonApiHandler.builder(new TypeReference<Integer>() {})
                .addBody(new TypeReference<Integer>() {})
                .addQueryParam("operand", new TypeReference<Integer>() {})
                .build(api::add);
        HttpHandler healthHandler = UndertowJsonApiHandler.builder().build(api::healthCheck);

        return UndertowApiRouter.builder()
                .addRoute("/add", addHandler)
                .addRoute("/health", healthHandler)
                .build();
    }

    /** Adds two numbers. */
    void add(Sender.Value<Integer> sender, int operand1, int operand2, Dispatcher dispatcher);

    /** Sends a 200 status. */
    void healthCheck(Sender.StatusCode sender, Dispatcher dispatcher);
}
