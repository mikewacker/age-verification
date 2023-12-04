package org.example.age.common.service.store.inmemory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.example.age.api.HttpOptional;
import org.example.age.api.JsonSerializer;
import org.example.age.common.service.store.PendingStore;
import org.xnio.XnioExecutor;

/**
 * In-memory {@link PendingStore}.
 *
 * <p>Serializing values is not necessary, but it is done to enforce the contract for value types.</p>
 */
final class InMemoryPendingStore<V> implements PendingStore<V> {

    private final JsonSerializer serializer;
    private final TypeReference<V> valueTypeRef;

    private final BiMap<String, ExpirableRawValue> store = Maps.synchronizedBiMap(HashBiMap.create());

    public InMemoryPendingStore(JsonSerializer serializer, TypeReference<V> valueTypeRef) {
        this.serializer = serializer;
        this.valueTypeRef = valueTypeRef;
    }

    /** Gets the value type. */
    public Type getValueType() {
        return valueTypeRef.getType();
    }

    @Override
    public void put(String key, V value, long expiration, XnioExecutor executor) {
        long now = System.currentTimeMillis() / 1000;
        long expiresIn = expiration - now;
        if (expiresIn <= 0) {
            return;
        }

        byte[] rawValue = serializer.serialize(value);
        ExpirableRawValue expirableRawValue = new ExpirableRawValue(rawValue);
        Optional<ExpirableRawValue> maybeOldExpirableRawValue = Optional.ofNullable(store.put(key, expirableRawValue));
        expirableRawValue.scheduleExpiration(expiresIn, executor);
        maybeOldExpirableRawValue.ifPresent(ExpirableRawValue::cancelExpiration);
    }

    @Override
    public Optional<V> tryGet(String key) {
        Optional<ExpirableRawValue> maybeExpirableRawValue = Optional.ofNullable(store.get(key));
        if (maybeExpirableRawValue.isEmpty()) {
            return Optional.empty();
        }
        ExpirableRawValue expirableRawValue = maybeExpirableRawValue.get();

        return tryDeserialize(expirableRawValue.get());
    }

    @Override
    public Optional<V> tryRemove(String key) {
        Optional<ExpirableRawValue> maybeExpirableRawValue = Optional.ofNullable(store.remove(key));
        if (maybeExpirableRawValue.isEmpty()) {
            return Optional.empty();
        }
        ExpirableRawValue expirableRawValue = maybeExpirableRawValue.get();

        expirableRawValue.cancelExpiration();
        return tryDeserialize(expirableRawValue.get());
    }

    /** Deserializes the raw value, or returns empty. */
    private Optional<V> tryDeserialize(byte[] rawValue) {
        HttpOptional<V> maybeValue = serializer.tryDeserialize(rawValue, valueTypeRef, 500);
        return maybeValue.toOptional();
    }

    /**
     * Raw value that expires.
     *
     * <p>Also ensures that {@link #equals(Object)} behaves like {@code ==}.</p>
     */
    private final class ExpirableRawValue {

        private static final XnioExecutor.Key NO_OP_KEY = () -> false;

        private final byte[] rawValue;
        private XnioExecutor.Key expirationKey = NO_OP_KEY;

        /** Gets the raw value. */
        byte[] get() {
            return rawValue;
        }

        /** Creates an expirable raw value whose expiration has not been scheduled. */
        public ExpirableRawValue(byte[] rawValue) {
            this.rawValue = rawValue;
        }

        /** Schedules a task to expire the value. */
        public void scheduleExpiration(long expiresIn, XnioExecutor executor) {
            expirationKey = executor.executeAfter(this::expire, expiresIn, TimeUnit.SECONDS);
        }

        /** Cancels the task to expire the value. */
        public void cancelExpiration() {
            expirationKey.remove();
        }

        /** Expires the value. */
        private void expire() {
            store.inverse().remove(this);
        }
    }
}
