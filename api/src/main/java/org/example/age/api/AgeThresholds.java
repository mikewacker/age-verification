package org.example.age.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.List;
import org.example.age.common.ValueStyle;
import org.immutables.value.Value;

/** Ordered list of age thresholds. */
@Value.Immutable
@ValueStyle
public interface AgeThresholds {

    /** Creates the age thresholds. */
    static AgeThresholds of(int... values) {
        return of(Arrays.stream(values).boxed().toList());
    }

    /** Creates the age thresholds. */
    @JsonCreator
    static AgeThresholds of(List<Integer> values) {
        checkValuesIncrease(values);
        return ImmutableAgeThresholds.builder().values(values).build();
    }

    /** Expands the user's age range so that it aligns with the closest age thresholds. */
    default VerifiedUser anonymize(VerifiedUser user) {
        AgeRange newAgeRange = anonymize(user.getAgeRange());
        return VerifiedUser.builder()
                .pseudonym(user.getPseudonym())
                .ageRange(newAgeRange)
                .guardianPseudonyms(user.getGuardianPseudonyms())
                .build();
    }

    /** Expands an age range so that it aligns with the closest age thresholds. */
    default AgeRange anonymize(AgeRange ageRange) {
        Integer newMin = alignMinWithThreshold(ageRange.getMin());
        Integer newMax = alignMaxWithThreshold(ageRange.getMax());
        return AgeRange.builder().min(newMin).max(newMax).build();
    }

    /** Gets the age thresholds. */
    @JsonValue
    List<Integer> values();

    /** Checks that at least value exists and each value is greater than the last. */
    private static void checkValuesIncrease(List<Integer> values) {
        if (values.isEmpty()) {
            throw new IllegalArgumentException("age thresholds must not be empty");
        }

        int lastValue = values.getFirst();
        for (int index = 1; index < values.size(); index++) {
            int value = values.get(index);
            if (value <= lastValue) {
                throw new IllegalArgumentException("age thresholds must increase");
            }

            lastValue = value;
        }
    }

    /** Aligns the minimum age with a threshold. */
    private Integer alignMinWithThreshold(Integer min) {
        if (min == null) {
            return null;
        }

        Integer newMin = null;
        for (int value : values()) {
            if (value > min) {
                return newMin;
            }

            newMin = value;
        }
        return newMin;
    }

    /** Aligns the maximum age with a threshold. */
    private Integer alignMaxWithThreshold(Integer max) {
        if (max == null) {
            return null;
        }

        for (int value : values()) {
            if (value > max) {
                return value - 1;
            }
        }
        return null;
    }
}
