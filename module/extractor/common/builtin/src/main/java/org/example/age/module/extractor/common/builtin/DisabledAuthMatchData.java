package org.example.age.module.extractor.common.builtin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.json.JsonStyle;
import org.immutables.value.Value;

/** {@link AuthMatchData} that always returns a successful match. */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableDisabledAuthMatchData.class)
public interface DisabledAuthMatchData extends AuthMatchData {

    /** Creates authentication data. */
    static AuthMatchData of() {
        return ImmutableDisabledAuthMatchData.builder().build();
    }

    @Override
    default boolean match(AuthMatchData other) {
        return true;
    }
}
