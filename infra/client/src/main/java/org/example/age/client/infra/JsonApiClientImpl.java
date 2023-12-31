package org.example.age.client.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.api.HttpOptional;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

final class JsonApiClientImpl extends OkHttpJsonApiClient implements JsonApiClient {

    private static final JsonApiClientImpl instance = new JsonApiClientImpl();

    private static final OkHttpClient client = new OkHttpClient();

    /** Creates a builder for a JSON API request whose response is only a status code. */
    public static UrlStageRequestBuilder<ExecuteStage<Integer>> requestBuilder() {
        return instance.requestBuilder(ExecuteStageImpl::new);
    }

    /** Creates a builder for a JSON API request whose response is a JSON value (or an error status code). */
    public static <V> UrlStageRequestBuilder<ExecuteStage<HttpOptional<V>>> requestBuilder(
            TypeReference<V> responseValueTypeRef) {
        return instance.requestBuilder(ExecuteStageImpl::new, responseValueTypeRef);
    }

    private JsonApiClientImpl() {}

    /** Internal {@link ExecuteStage} implementation. */
    private static final class ExecuteStageImpl<V> implements ExecuteStage<V> {

        private final Request request;
        private final ResponseConverter<V> responseConverter;

        private final AtomicBoolean wasExecuted = new AtomicBoolean(false);

        @Override
        public V execute() throws IOException {
            if (wasExecuted.getAndSet(true)) {
                throw new IllegalStateException("request was already executed");
            }

            Response response = client.newCall(request).execute();
            return responseConverter.convert(response);
        }

        private ExecuteStageImpl(Request request, ResponseConverter<V> responseConverter) {
            this.request = request;
            this.responseConverter = responseConverter;
        }
    }
}
