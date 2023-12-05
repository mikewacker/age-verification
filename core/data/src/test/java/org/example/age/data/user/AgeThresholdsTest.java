package org.example.age.data.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.testing.EqualsTester;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public final class AgeThresholdsTest {

    @Test
    public void of() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        assertThat(ageThresholds.get()).containsExactly(13, 18);
        assertThat(ageThresholds.toAgeRanges())
                .containsExactly(AgeRange.below(13), AgeRange.of(13, 18), AgeRange.atOrAbove(18));
        assertThat(ageThresholds.toString()).isEqualTo("AgeThresholds[13, 18]");
    }

    @Test
    public void anonymize_MiddleAgeRange() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.at(16), AgeRange.of(13, 18));
    }

    @Test
    public void anonymize_FirstAgeRange() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.at(10), AgeRange.below(13));
    }

    @Test
    public void anonymize_LastAgeRange() {
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
    public void anonymize_CrossesAgeThreshold() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.of(10, 16), AgeRange.below(18));
    }

    @Test
    public void anonymize_CrossesAllAgeThresholds() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        anonymize(ageThresholds, AgeRange.of(10, 21), AgeRange.anyAge());
    }

    private void anonymize(AgeThresholds ageThresholds, AgeRange ageRange, AgeRange expectedAnonymizedAgeRange) {
        AgeRange anonymizedAgeRange = ageThresholds.anonymize(ageRange);
        assertThat(anonymizedAgeRange).isEqualTo(expectedAnonymizedAgeRange);
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        byte[] rawAgeThresholds = mapper.writeValueAsBytes(ageThresholds);
        AgeThresholds rtAgeThresholds = mapper.readValue(rawAgeThresholds, new TypeReference<>() {});
        assertThat(rtAgeThresholds).isEqualTo(ageThresholds);
    }

    @Test
    public void equals() {
        new EqualsTester()
                .addEqualityGroup(AgeThresholds.of(13, 18), AgeThresholds.of(13, 18))
                .addEqualityGroup(AgeThresholds.of(18))
                .testEquals();
    }

    @Test
    public void copyOnCreate() {
        List<Integer> underlyingAgeThresholds = new ArrayList<>();
        underlyingAgeThresholds.addAll(List.of(13, 18));
        AgeThresholds ageThresholds = AgeThresholds.of(underlyingAgeThresholds);
        underlyingAgeThresholds.set(0, 19);
        assertThat(ageThresholds.get()).containsExactly(13, 18);
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
    public void error_GetAndModify() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        List<Integer> underlyingAgeThresholds = ageThresholds.get();
        assertThatThrownBy(() -> underlyingAgeThresholds.set(0, 19)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void error_ToAgeRangesAndModify() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        List<AgeRange> ageRanges = ageThresholds.toAgeRanges();
        assertThatThrownBy(() -> ageRanges.add(AgeRange.anyAge())).isInstanceOf(UnsupportedOperationException.class);
    }
}
