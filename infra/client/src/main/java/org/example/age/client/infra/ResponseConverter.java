package org.example.age.client.infra;

import java.io.IOException;
import okhttp3.Response;

/** Converts the {@link Response} to the return value. */
@FunctionalInterface
public interface ResponseConverter<V> {

    V convert(Response response) throws IOException;
}
