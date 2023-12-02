package org.example.age.data.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

/** Ordered list of age thresholds. */
public final class AgeThresholds {

    private final List<Integer> ageThresholds;

    /** Creates the age thresholds. */
    @JsonCreator
    public static AgeThresholds of(List<Integer> ageThresholds) {
        return new AgeThresholds(ageThresholds);
    }

    /** Creates the age thresholds. */
    public static AgeThresholds of(int... ageThresholds) {
        return of(Ints.asList(ageThresholds));
    }

    /** Gets the underlying age thresholds. */
    @JsonValue
    public List<Integer> get() {
        return ageThresholds;
    }

    /** Anonymizes an age range, expanding it to align with the nearest age thresholds. */
    public AgeRange anonymize(AgeRange ageRange) {
        int minAge = findNearestMinAgeThreshold(ageRange.minAge());
        int maxAge = findNearestMaxAgeThreshold(ageRange.maxAge());
        return AgeRange.of(minAge, maxAge);
    }

    /** Converts these {@link AgeThresholds} to a list of {@link AgeRange}'s. */
    public List<AgeRange> toAgeRanges() {
        // Add the first age range.
        List<AgeRange> ageRanges = new ArrayList<>();
        int prevAgeThreshold = ageThresholds.get(0);
        AgeRange ageRange = AgeRange.below(prevAgeThreshold);
        ageRanges.add(ageRange);

        // Add age ranges in the middle.
        for (int ageThreshold : ageThresholds.subList(1, ageThresholds.size())) {
            ageRange = AgeRange.of(prevAgeThreshold, ageThreshold);
            ageRanges.add(ageRange);
            prevAgeThreshold = ageThreshold;
        }

        // Add the last age range.
        ageRange = AgeRange.atOrAbove(prevAgeThreshold);
        ageRanges.add(ageRange);
        return Collections.unmodifiableList(ageRanges);
    }

    @Override
    public boolean equals(Object o) {
        AgeThresholds other = (o instanceof AgeThresholds) ? (AgeThresholds) o : null;
        if (other == null) {
            return false;
        }

        return ageThresholds.equals(other.ageThresholds);
    }

    @Override
    public int hashCode() {
        return ageThresholds.hashCode();
    }

    @Override
    public String toString() {
        return String.format("AgeThresholds%s", ageThresholds);
    }

    private AgeThresholds(List<Integer> ageThresholds) {
        this.ageThresholds = List.copyOf(ageThresholds);
        checkAgeThresholds();
    }

    /** Checks that the age thresholds start at 1 or above and increase. */
    private void checkAgeThresholds() {
        if (ageThresholds.isEmpty()) {
            throw new IllegalArgumentException("must have at least one age threshold");
        }

        int prevAgeThreshold = ageThresholds.get(0);
        if (prevAgeThreshold < 1) {
            String message = String.format("first age threshold must be at least 1: %s", ageThresholds);
            throw new IllegalArgumentException(message);
        }

        for (int ageThreshold : ageThresholds.subList(1, ageThresholds.size())) {
            if (ageThreshold <= prevAgeThreshold) {
                String message =
                        String.format("each age threshold must be greater than the previous one: %s", ageThresholds);
                throw new IllegalArgumentException(message);
            }

            prevAgeThreshold = ageThreshold;
        }
    }

    /** Finds the closest min age threshold. */
    private int findNearestMinAgeThreshold(OptionalInt maybeMinAge) {
        if (maybeMinAge.isEmpty()) {
            return AgeRange.FLOOR;
        }
        int minAge = maybeMinAge.getAsInt();

        int prevAgeThreshold = AgeRange.FLOOR;
        for (int index = 0; index < ageThresholds.size(); index++) {
            int ageThreshold = ageThresholds.get(index);
            if (ageThreshold > minAge) {
                return prevAgeThreshold;
            }

            prevAgeThreshold = ageThreshold;
        }
        return prevAgeThreshold;
    }

    /** Finds the closest max age threshold. */
    private int findNearestMaxAgeThreshold(OptionalInt maybeMaxAge) {
        if (maybeMaxAge.isEmpty()) {
            return AgeRange.CEILING;
        }
        int maxAge = maybeMaxAge.getAsInt();

        int nextAgeThreshold = AgeRange.CEILING;
        for (int index = ageThresholds.size() - 1; index >= 0; index--) {
            int ageThreshold = ageThresholds.get(index);
            if (ageThreshold < maxAge) {
                return nextAgeThreshold;
            }

            nextAgeThreshold = ageThreshold;
        }
        return nextAgeThreshold;
    }
}
