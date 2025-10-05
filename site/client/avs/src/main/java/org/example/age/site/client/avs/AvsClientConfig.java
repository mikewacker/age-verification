package org.example.age.site.client.avs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URL;
import org.example.age.common.annotation.ValueStyle;
import org.immutables.value.Value;

/** Configuration for the age verification service client. */
@Value.Immutable
@ValueStyle
@JsonSerialize
@JsonDeserialize(as = ImmutableAvsClientConfig.class)
public interface AvsClientConfig {

    /** Creates a builder for the configuration. */
    static Builder builder() {
        return new Builder();
    }

    /** URL of the age verification service. */
    URL url();

    /** Builder for the configuration. */
    final class Builder extends ImmutableAvsClientConfig.Builder {

        Builder() {}
    }
}
