package org.example.age.testing.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.example.age.api.base.HttpOptional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class FakeValueSenderTest {

    private FakeValueSender<String> sender;

    @BeforeEach
    public void createJsonSender() {
        sender = FakeValueSender.create();
    }

    @Test
    public void sendAndGet() {
        sender.sendValue("test");
        assertThat(sender.tryGet()).hasValue(HttpOptional.of("test"));
    }

    @Test
    public void tryGet() {
        assertThat(sender.tryGet()).isEmpty();
    }

    @Test
    public void error_SendTwice() {
        sender.sendValue("test");
        assertThatThrownBy(() -> sender.sendValue("test"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("response was already sent");
    }
}
