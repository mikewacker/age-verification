package org.example.age.server.undertow;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.server.handlers.resource.ResourceManager;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

/** Factory that creates an HTTP handler that serves static HTML files. */
final class HtmlHandlerFactory {

    /** Creates an HTTP handler that serves static HTML files, using the class to find the resources. */
    public static HttpHandler create(Class<?> clazz) {
        Path rootDir = getRootDir(clazz);
        ResourceManager resourceManager = new PathResourceManager(rootDir);
        return Handlers.resource(resourceManager);
    }

    /** Gets the root directory for the static HTML files: {@code resources/html}. */
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
    private HtmlHandlerFactory() {}
}
