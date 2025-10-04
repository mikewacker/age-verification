package org.example.age.common.client.api;

import java.net.URL;

/** Factory for API clients. */
@FunctionalInterface
public interface ApiClientFactory {

    <A> A create(URL baseUrl, Class<A> apiType);
}
