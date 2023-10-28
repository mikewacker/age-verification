package org.example.age.common.server.html;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceManager;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/** Serves static HTML files. */
final class HtmlHandler {

    /** Creates an HTTP handler that serves static HTML files. */
    public static HttpHandler create(Class<?> clazz) {
        Path rootDir = getRootDir(clazz);
        ResourceManager resourceManager = new PathResourceManager(rootDir);
        return Handlers.resource(resourceManager);
    }

    /** Gets the root directory for the static HTML files. */
    private static Path getRootDir(Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        URL url = classLoader.getResource("html");
        try {
            return Path.of(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // static class
    private HtmlHandler() {}
}
