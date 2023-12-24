package org.example.age.demo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class MainTest {

    @BeforeAll
    public static void redirectStdoutToNull() {
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
    }

    @Test
    public void runDemo() throws IOException {
        Main.main(new String[0]);
    }
}
