package org.example.age.demo;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/** Locates resource files. */
public class Resources {

    private static final ClassLoader classLoader = Resources.class.getClassLoader();

    /** Gets the absolute path to a resource file. */
    public static String get(String relativePath) throws URISyntaxException {
        URI fileUri = classLoader.getResource(relativePath).toURI();
        return new File(fileUri).getAbsolutePath();
    }

    private Resources() {} // static class
}
