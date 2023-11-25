package org.example.age.common.service.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.data.utils.DataStyle;
import org.immutables.value.Value;

/** {@link AuthMatchData} that always returns a successful match. */
@Value.Immutable
@DataStyle
@JsonSerialize(as = ImmutableDisabledAuthMatchData.class)
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
