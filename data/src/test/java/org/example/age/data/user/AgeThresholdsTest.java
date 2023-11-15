package org.example.age.data.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

public final class AgeThresholdsTest {

    @Test
    public void of() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        assertThat(ageThresholds.getAgeRanges())
                .containsExactly(AgeRange.below(13), AgeRange.of(13, 18), AgeRange.atOrAbove(18));
    }

    @Test
    public void anonymize_Middle() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.at(16), AgeRange.of(13, 18));
    }

    @Test
    public void anonymize_First() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.at(10), AgeRange.below(13));
    }

    @Test
    public void anonymize_Last() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.at(21), AgeRange.atOrAbove(18));
    }

    @Test
    public void anonymize_Boundaries() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.at(12), AgeRange.below(13));
        anonymize(ageThresholds, AgeRange.at(13), AgeRange.of(13, 18));
        anonymize(ageThresholds, AgeRange.at(17), AgeRange.of(13, 18));
        anonymize(ageThresholds, AgeRange.at(18), AgeRange.atOrAbove(18));
    }

    @Test
    public void anonymize_CrossesThreshold() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.of(10, 16), AgeRange.below(18));
    }

    @Test
    public void anonymize_CrossesThreshold_NoMinAge() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.below(16), AgeRange.below(18));
    }

    @Test
    public void anonymize_CrossesThreshold_NoMaxAge() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.atOrAbove(16), AgeRange.atOrAbove(13));
    }

    @Test
    public void anonymize_CrossesAllThresholds() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.of(10, 21), AgeRange.anyAge());
    }

    private void anonymize(AgeThresholds ageThresholds, AgeRange ageRange, AgeRange expectedAnonymizedAgeRange) {
        AgeRange anonymizedAgeRange = ageThresholds.anonymize(ageRange);
        assertThat(anonymizedAgeRange).isEqualTo(expectedAnonymizedAgeRange);
    }

    @Test
    public void error_IllegalAgeThresholds_NoAgeThresholds() {
        error_IllegalAgeThresholds(List.of(), "must have at least one age threshold");
    }

    @Test
    public void error_IllegalAgeThresholds_FirstAgeThresholdIsZero() {
        error_IllegalAgeThresholds(List.of(0), "first age threshold must be at least 1: [0]");
    }

    @Test
    public void error_IllegalAgeThresholds_AgeThresholdsDoNotIncrease() {
        error_IllegalAgeThresholds(
                List.of(13, 18, 18), "each age threshold must be greater than the previous one: [13, 18, 18]");
    }

    private void error_IllegalAgeThresholds(List<Integer> ageThresholds, String expectedMessage) {
        assertThatThrownBy(() -> AgeThresholds.of(ageThresholds))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void error_GetAndModifyAgeRanges() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        List<AgeRange> ageRanges = ageThresholds.getAgeRanges();
        assertThatThrownBy(() -> ageRanges.add(AgeRange.atOrAbove(21)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
