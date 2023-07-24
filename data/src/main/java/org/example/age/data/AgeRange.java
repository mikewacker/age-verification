package org.example.age.data;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.Objects;
import java.util.OptionalInt;

/** Age range, where the min age is inclusive and the max age is exclusive. */
@JsonSerialize(using = ToStringSerializer.class)
@JsonDeserialize(using = AgeRange.Deserializer.class)
public final class AgeRange {

    private static final int FLOOR = 0;
    private static final int CEILING = Integer.MAX_VALUE;

    private final int minAge;
    private final int maxAge;

    /** Creates a range between the min age (inclusive) and the max age (exclusive). */
    public static AgeRange of(int minAge, int maxAge) {
        checkAgeRange(minAge, maxAge);
        return new AgeRange(minAge, maxAge);
    }

    /** Creates a range for an exact age. */
    public static AgeRange at(int age) {
        return of(age, age + 1);
    }

    /** Creates a range at or above the min age. */
    public static AgeRange atOrAbove(int minAge) {
        return of(minAge, CEILING);
    }

    /** Creates a range below the max age. */
    public static AgeRange below(int maxAge) {
        return of(FLOOR, maxAge);
    }

    /** Creates a range that covers any age. */
    public static AgeRange anyAge() {
        return of(FLOOR, CEILING);
    }

    /** Gets the min age (inclusive), or empty if no min exists. */
    public OptionalInt minAge() {
        return (minAge != 0) ? OptionalInt.of(minAge) : OptionalInt.empty();
    }

    /** Gets the max age (exclusive), or empty if no max exists. */
    public OptionalInt maxAge() {
        return (maxAge != CEILING) ? OptionalInt.of(maxAge) : OptionalInt.empty();
    }

    /** Determines if the min age meets the age threshold. */
    public boolean meetsThreshold(int ageThreshold) {
        return minAge >= ageThreshold;
    }

    @Override
    public boolean equals(Object o) {
        AgeRange other = (o instanceof AgeRange) ? (AgeRange) o : null;
        if (other == null) {
            return false;
        }

        return (minAge == other.minAge) && (maxAge == other.maxAge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minAge, maxAge);
    }

    @Override
    public String toString() {
        if (maxAge - minAge == 1) {
            return Integer.toString(minAge);
        }

        if (maxAge == CEILING) {
            return String.format("%d+", minAge);
        }

        if (minAge == FLOOR) {
            return String.format("%d-", maxAge - 1);
        }

        return String.format("%d-%d", minAge, maxAge - 1);
    }

    /** Checks that the age range starts at 0 or above and increases. */
    private static void checkAgeRange(int minAge, int maxAge) {
        if (minAge < 0) {
            String message = String.format("min age must be at least 0: %d", minAge);
            throw new IllegalArgumentException(message);
        }

        if (maxAge <= minAge) {
            String message = String.format("max age must be greater than min age: [%d, %d)", minAge, maxAge);
            throw new IllegalArgumentException(message);
        }
    }

    private AgeRange(int minAge, int maxAge) {
        this.minAge = minAge;
        this.maxAge = maxAge;
    }

    /** JSON {@code fromString()} deserializer. */
    static final class Deserializer extends FromStringDeserializer<AgeRange> {

        public Deserializer() {
            super(AgeRange.class);
        }

        @Override
        protected AgeRange _deserialize(String value, DeserializationContext context) {
            if (value.endsWith("+")) {
                String minValue = value.substring(0, value.length() - 1);
                int minAge = parseMinAge(minValue);
                return AgeRange.atOrAbove(minAge);
            }

            if (value.endsWith("-")) {
                String maxValue = value.substring(0, value.length() - 1);
                int maxAge = parseMaxAge(maxValue);
                return AgeRange.below(maxAge);
            }

            if (!value.contains("-")) {
                int age = parseMinAge(value);
                return AgeRange.at(age);
            }

            int dashIndex = value.indexOf("-");
            String minValue = value.substring(0, dashIndex);
            String maxValue = value.substring(dashIndex + 1);
            int minAge = parseMinAge(minValue);
            int maxAge = parseMaxAge(maxValue);
            return AgeRange.of(minAge, maxAge);
        }

        private static int parseMinAge(String minValue) {
            return Integer.parseInt(minValue);
        }

        private static int parseMaxAge(String maxValue) {
            return Integer.parseInt(maxValue) + 1;
        }
    }
}
