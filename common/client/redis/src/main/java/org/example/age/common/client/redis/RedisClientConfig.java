package org.example.age.common.client.redis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.net.URI;
import org.example.age.common.annotation.ValueStyle;
import org.immutables.value.Value;

/** Configuration for the Redis client. */
@Value.Immutable
@ValueStyle
@JsonDeserialize(as = ImmutableRedisClientConfig.class)
public interface RedisClientConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** URI of Redis. */
    URI uri();

    /** Builder for the configuration. */
    final class Builder extends ImmutableRedisClientConfig.Builder {

        Builder() {}
    }
}
