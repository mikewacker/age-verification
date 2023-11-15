package org.example.age.data.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

/** Ordered list of age thresholds. */
public final class AgeThresholds {

    private final List<AgeRange> ageRanges;

    /** Creates the age thresholds. */
    public static AgeThresholds of(List<Integer> ageThresholds) {
        ageThresholds = new ArrayList<>(ageThresholds);
        checkAgeThresholds(ageThresholds);
        List<AgeRange> ageRanges = createAgeRanges(ageThresholds);
        return new AgeThresholds(ageRanges);
    }

    /** Creates the age thresholds. */
    public static AgeThresholds of(int... ageThresholds) {
        return of(Arrays.stream(ageThresholds).boxed().toList());
    }

    /** Gets an immutable ordered list of the corresponding age ranges. */
    public List<AgeRange> getAgeRanges() {
        return ageRanges;
    }

    /** Anonymizes an age range, expanding it to align with the nearest age thresholds. */
    public AgeRange anonymize(AgeRange ageRange) {
        int minIndex = findAgeRangeForMinAge(ageRange.minAge());
        int maxIndex = findAgeRangeForMaxAge(ageRange.maxAge(), minIndex);
        return (minIndex == maxIndex) ? ageRanges.get(minIndex) : createAgeRangeCrossingThreshold(minIndex, maxIndex);
    }

    /** Checks that the age thresholds start at 1 or above and increase. */
    private static void checkAgeThresholds(List<Integer> ageThresholds) {
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

    /** Creates the corresponding age ranges from the age thresholds. */
    private static List<AgeRange> createAgeRanges(List<Integer> ageThresholds) {
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

    /** Finds the index of the age range that contains the min age. */
    private int findAgeRangeForMinAge(OptionalInt maybeMinAge) {
        if (maybeMinAge.isEmpty()) {
            return 0;
        }
        int minAge = maybeMinAge.getAsInt();

        for (int index = 0; index < ageRanges.size() - 1; index++) {
            AgeRange ageRange = ageRanges.get(index);
            if (minAge < ageRange.maxAge().getAsInt()) {
                return index;
            }
        }
        return ageRanges.size() - 1;
    }

    /** Finds the index of the age range that contains the max age. */
    private int findAgeRangeForMaxAge(OptionalInt maybeMaxAge, int beginIndex) {
        if (maybeMaxAge.isEmpty()) {
            return ageRanges.size() - 1;
        }
        int maxAge = maybeMaxAge.getAsInt();

        for (int index = beginIndex; index < ageRanges.size() - 1; index++) {
            AgeRange ageRange = ageRanges.get(index);
            if (maxAge <= ageRange.maxAge().getAsInt()) {
                return index;
            }
        }
        return ageRanges.size() - 1;
    }

    /** Creates an age range that crosses one or more thresholds. */
    private AgeRange createAgeRangeCrossingThreshold(int minIndex, int maxIndex) {
        // Get the min and (maybe) the max age.
        AgeRange minAgeRange = ageRanges.get(minIndex);
        OptionalInt maybeMinAge = minAgeRange.minAge();
        int minAge = maybeMinAge.isPresent() ? maybeMinAge.getAsInt() : 0;

        AgeRange maxAgeRange = ageRanges.get(maxIndex);
        OptionalInt maybeMaxAge = maxAgeRange.maxAge();

        // Create the age range.
        return maybeMaxAge.isPresent() ? AgeRange.of(minAge, maybeMaxAge.getAsInt()) : AgeRange.atOrAbove(minAge);
    }

    private AgeThresholds(List<AgeRange> ageRanges) {
        this.ageRanges = ageRanges;
    }
}
