package org.example.age.service.infra.client.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import dagger.Component;
import io.undertow.server.HttpHandler;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Singleton;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.example.age.api.base.Dispatcher;
import org.example.age.api.base.Sender;
import org.example.age.client.infra.JsonApiClient;
import org.example.age.data.json.JsonValues;
import org.example.age.testing.server.TestServer;

/** Test service for {@link GreetingApi} that uses a {@link DispatcherOkHttpClient}. */
public final class GreetingService implements GreetingApi {

    private final DispatcherOkHttpClient client = TestComponent.createDispatcherOkHttpClient();
    private final TestServer<?> backendServer = TestServer.get("backend");

    /** Creates an {@link HttpHandler} from a {@link GreetingService}. */
    public static HttpHandler createHandler() {
        GreetingApi api = new GreetingService();
        return GreetingApi.createHandler(api);
    }

    @Override
    public void greeting(Sender.Value<String> sender, Dispatcher dispatcher) {
        Callback callback = new RecipientCallback(sender, dispatcher);
        JsonApiClient.requestBuilder(client.get(dispatcher))
                .get(backendServer.rootUrl())
                .enqueue(callback);
        dispatcher.dispatched();
    }

    private GreetingService() {}

    /** Callback for the backend request to get a recipient. */
    private record RecipientCallback(Sender.Value<String> sender, Dispatcher dispatcher) implements Callback {

        @Override
        public void onResponse(Call call, Response response) {
            dispatcher.executeHandler(() -> onResponse(response));
        }

        @Override
        public void onFailure(Call call, IOException e) {
            dispatcher.executeHandler(this::onFailure);
        }

        /** Callback for a response. */
        private void onResponse(Response response) {
            Optional<String> maybeRecipient = tryGetRecipient(response);
            if (maybeRecipient.isEmpty()) {
                onFailure();
                return;
            }
            String recipient = maybeRecipient.get();

            String greeting = String.format("Hello, %s!", recipient);
            sender.sendValue(greeting);
        }

        /** Callback for a failure. */
        private void onFailure() {
            sender.sendErrorCode(502);
        }

        /** Gets the recipient from the response, or returns empty. */
        private static Optional<String> tryGetRecipient(Response response) {
            try {
                byte[] rawRecipient = response.body().bytes();
                return JsonValues.tryDeserialize(rawRecipient, new TypeReference<>() {});
            } catch (IOException e) {
                return Optional.empty();
            }
        }
    }

    /** Dagger component that provides a {@link DispatcherOkHttpClient}. */
    @Component(modules = DispatcherOkHttpClientModule.class)
    @Singleton
    interface TestComponent {

        static DispatcherOkHttpClient createDispatcherOkHttpClient() {
            TestComponent component = DaggerGreetingService_TestComponent.create();
            return component.dispatcherOkHttpClient();
        }

        DispatcherOkHttpClient dispatcherOkHttpClient();
    }
}
