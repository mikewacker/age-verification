package org.example.age.module.extractor.builtin;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.mikewacker.drift.json.JsonStyle;
import org.example.age.api.def.AuthMatchData;
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
