package org.example.age.testing.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.age.api.HttpOptional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class FakeJsonSenderTest {

    private FakeJsonSender<String> sender;

    @BeforeEach
    public void createJsonSender() {
        sender = FakeJsonSender.create();
    }

    @Test
    public void sendAndGet() {
        assertThat(sender.tryGet()).isEmpty();
        sender.sendBody("test");
        assertThat(sender.tryGet()).hasValue(HttpOptional.of("test"));
    }

    @Test
    public void error_SendTwice() {
        sender.sendBody("test");
        assertThatThrownBy(() -> sender.sendBody("test"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("response was already sent");
    }
}
