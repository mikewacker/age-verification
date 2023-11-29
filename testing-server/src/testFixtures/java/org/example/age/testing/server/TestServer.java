package org.example.age.testing.server;

import com.google.errorprone.annotations.FormatMethod;

/**
 * Extension that starts a test server.
 *
 * <p>This extension will be a static field, though in some cases a fresh server may be started for each test.</p>
 */
public interface TestServer<T> {

    /** Gets the underlying server. */
    T get();

    /** Gets the host of the server. */
    String host();

    /** Gets the port of the server. */
    int port();

    /** Gets the root URL for the server. */
    String rootUrl();

    /** Gets the URL at the provided path. */
    @SuppressWarnings("FormatStringAnnotation")
    default String url(String path) {
        return url(path, new Object[0]);
    }

    /** Gets the URL at the provided path. */
    @FormatMethod
    default String url(String pathFormat, Object... args) {
        pathFormat = pathFormat.replaceFirst("^/", "");
        String path = String.format(pathFormat, args);
        return String.format("%s/%s", rootUrl(), path);
    }
}
