package org.example.age.testing;

import com.google.common.net.HostAndPort;

/**
 * Extension that starts a test server.
 *
 * <p>This extension will be a static field, though in some cases a fresh server may be started for each test.</p>
 */
public interface TestServer<T> {

    /** Gets the underlying server. */
    T get();

    /** Gets the host and port of the server. */
    HostAndPort hostAndPort();

    /** Gets the root URL for the server. */
    String rootUrl();

    /** Gets the URL at the provided path. */
    default String url(String path) {
        path = path.startsWith("/") ? path : String.format("/%s", path);
        return String.format("%s%s", rootUrl(), path);
    }
}
