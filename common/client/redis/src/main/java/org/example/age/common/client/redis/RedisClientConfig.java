package org.example.age.common.client.redis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URL;
import org.example.age.common.annotation.ValueStyle;
import org.immutables.value.Value;

/** Configuration for the Redis client. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableRedisClientConfig.class)
public interface RedisClientConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** URL of Redis. */
    URL url();

    /** Builder for the configuration. */
    final class Builder extends ImmutableRedisClientConfig.Builder {

        Builder() {}
    }
}
