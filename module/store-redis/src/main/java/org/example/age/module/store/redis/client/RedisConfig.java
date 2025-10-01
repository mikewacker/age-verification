package org.example.age.module.store.redis.client;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URL;
import org.example.age.common.util.ValueStyle;
import org.immutables.value.Value;

/** Configuration for Redis. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableRedisConfig.class)
public interface RedisConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** URL of Redis. */
    URL url();

    /** Builder for the configuration. */
    final class Builder extends ImmutableRedisConfig.Builder {

        Builder() {}
    }
}
