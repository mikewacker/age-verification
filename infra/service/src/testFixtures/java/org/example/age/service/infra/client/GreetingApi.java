package org.example.age.service.infra.client;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.Dispatcher;
import io.github.mikewacker.drift.api.Sender;
import io.undertow.server.HttpHandler;
import org.example.age.api.infra.UndertowJsonApiHandler;

/** Asynchronous API that sends a greeting. */
public interface GreetingApi {

    /** Creates an {@link HttpHandler} from a {@link GreetingApi}. */
    static HttpHandler createHandler(GreetingApi api) {
        return UndertowJsonApiHandler.builder(new TypeReference<String>() {}).build(api::greeting);
    }

    /** Sends a greeting. */
    void greeting(Sender.Value<String> sender, Dispatcher dispatcher);
}
