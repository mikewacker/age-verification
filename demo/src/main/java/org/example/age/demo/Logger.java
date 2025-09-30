package org.example.age.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.errorprone.annotations.FormatMethod;
import java.io.IOException;
import org.example.age.testing.util.TestObjectMapper;

/** Console logger. */
public class Logger {

    private static final ObjectWriter writer = TestObjectMapper.get()
            .copy()
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
            .writerWithDefaultPrettyPrinter();
    private static boolean isVerbose = false;

    /** Sets whether verbose messages are logged. */
    public static void setVerbose(boolean isVerbose) {
        Logger.isVerbose = isVerbose;
    }

    /** Logs a message. */
    @FormatMethod
    public static void info(String format, Object... args) {
        String msg = String.format(format, args);
        System.out.println(msg);
    }

    /** Logs a verbose message. */
    @FormatMethod
    public static void verbose(String format, Object... args) {
        if (!isVerbose) {
            return;
        }

        String msg = String.format(format, args);
        System.out.println(msg);
    }

    /** Logs a value as JSON. */
    public static void json(Object value) throws IOException {
        String json = writer.writeValueAsString(value);
        System.out.println(json);
    }

    private Logger() {} // static class
}
