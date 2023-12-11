package org.example.age.module.extractor.common.builtin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.api.common.AuthMatchData;
import org.example.age.data.json.JsonStyle;
import org.immutables.value.Value;

/** {@link AuthMatchData} that matches the {@code User-Agent} header. */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableUserAgentAuthMatchData.class)
public interface UserAgentAuthMatchData extends AuthMatchData {

    /** Creates authentication data. */
    static AuthMatchData of(String userAgent) {
        return ImmutableUserAgentAuthMatchData.builder().userAgent(userAgent).build();
    }

    /** User agent. */
    String userAgent();

    @Override
    default boolean match(AuthMatchData other) {
        return equals(other);
    }
}
