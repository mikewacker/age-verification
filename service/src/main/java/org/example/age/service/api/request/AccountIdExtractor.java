package org.example.age.service.api.request;

/** Extracts an account ID from an HTTP request. */
@FunctionalInterface
public interface AccountIdExtractor {

    String getForRequest();
}
