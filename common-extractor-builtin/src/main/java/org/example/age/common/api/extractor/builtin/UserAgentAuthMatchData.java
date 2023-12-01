package org.example.age.common.api.extractor.builtin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.utils.DataStyle;
import org.immutables.value.Value;

/** {@link AuthMatchData} that matches the {@code User-Agent} header. */
@Value.Immutable
@DataStyle
@JsonSerialize(as = ImmutableUserAgentAuthMatchData.class)
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
