package org.example.age.module.store.inmemory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class BiKeyMapTest {

    private BiKeyMap<String, Integer, String> map;

    @BeforeEach
    public void createBiKeyMap() {
        map = BiKeyMap.create();
    }

    @Test
    public void putAndGet_NewSecondaryKey() {
        Optional<String> maybeConflictingKey = map.tryPut("key", Optional.of(1), "value");
        assertThat(maybeConflictingKey).isEmpty();
        assertThat(map.tryGet("key")).hasValue("value");
    }

    @Test
    public void putAndGet_SameSecondaryKey() {
        map.tryPut("key", Optional.of(1), "value1");
        assertThat(map.tryGet("key")).hasValue("value1");

        Optional<String> maybeConflictingKey = map.tryPut("key", Optional.of(1), "value2");
        assertThat(maybeConflictingKey).isEmpty();
        assertThat(map.tryGet("key")).hasValue("value2");
    }

    @Test
    public void putFails_ConflictingKey() {
        map.tryPut("key1", Optional.of(1), "value1");
        assertThat(map.tryGet("key1")).hasValue("value1");

        Optional<String> maybeConflictingKey = map.tryPut("key2", Optional.of(1), "value2");
        assertThat(maybeConflictingKey).hasValue("key1");
        assertThat(map.tryGet("key2")).isEmpty();
    }

    @Test
    public void freeSecondaryKey_Remove() {
        map.tryPut("key1", Optional.of(1), "value1");
        assertThat(map.tryGet("key1")).hasValue("value1");

        map.remove("key1");
        Optional<String> maybeConflictingKey = map.tryPut("key2", Optional.of(1), "value2");
        assertThat(maybeConflictingKey).isEmpty();
        assertThat(map.tryGet("key2")).hasValue("value2");
    }

    @Test
    public void freeSecondaryKey_RemoveSecondaryKey() {
        map.tryPut("key1", Optional.of(1), "value1");
        assertThat(map.tryGet("key1")).hasValue("value1");

        map.tryPut("key1", Optional.empty(), "value1");
        Optional<String> maybeConflictingKey = map.tryPut("key2", Optional.of(1), "value2");
        assertThat(maybeConflictingKey).isEmpty();
        assertThat(map.tryGet("key2")).hasValue("value2");
    }

    @Test
    public void freeSecondaryKey_UpdateSecondaryKey() {
        map.tryPut("key1", Optional.of(1), "value1");
        assertThat(map.tryGet("key1")).hasValue("value1");

        map.tryPut("key1", Optional.of(2), "value1");
        Optional<String> maybeConflictingKey = map.tryPut("key2", Optional.of(1), "value2");
        assertThat(maybeConflictingKey).isEmpty();
        assertThat(map.tryGet("key2")).hasValue("value2");
    }
}
