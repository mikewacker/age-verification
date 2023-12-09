package org.example.age.data.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.testing.EqualsTester;
import org.example.age.data.json.JsonValues;
import org.junit.jupiter.api.Test;

public final class AgeRangeTest {

    @Test
    public void of() {
        AgeRange ageRange = AgeRange.of(13, 18);
        assertThat(ageRange.minAge()).hasValue(13);
        assertThat(ageRange.maxAge()).hasValue(18);
        assertThat(ageRange.toString()).isEqualTo("13-17");
    }

    @Test
    public void at() {
        AgeRange ageRange = AgeRange.at(18);
        assertThat(ageRange.minAge()).hasValue(18);
        assertThat(ageRange.maxAge()).hasValue(19);
        assertThat(ageRange.toString()).isEqualTo("18");
    }

    @Test
    public void atOrAbove() {
        AgeRange ageRange = AgeRange.atOrAbove(18);
        assertThat(ageRange.minAge()).hasValue(18);
        assertThat(ageRange.maxAge()).isEmpty();
        assertThat(ageRange.toString()).isEqualTo("18+");
    }

    @Test
    public void below() {
        AgeRange ageRange = AgeRange.below(13);
        assertThat(ageRange.minAge()).isEmpty();
        assertThat(ageRange.maxAge()).hasValue(13);
        assertThat(ageRange.toString()).isEqualTo("12-");
    }

    @Test
    public void anyAge() {
        AgeRange ageRange = AgeRange.anyAge();
        assertThat(ageRange.minAge()).isEmpty();
        assertThat(ageRange.maxAge()).isEmpty();
        assertThat(ageRange.toString()).isEqualTo("0+");
    }

    @Test
    public void of_FloorAndCeiling() {
        AgeRange ageRange = AgeRange.of(AgeRange.FLOOR, AgeRange.CEILING);
        assertThat(ageRange.minAge()).isEmpty();
        assertThat(ageRange.maxAge()).isEmpty();
        assertThat(ageRange.toString()).isEqualTo("0+");
    }

    @Test
    public void meetsAgeThreshold_Met() {
        AgeRange ageRange = AgeRange.of(13, 18);
        meetsAgeThreshold(ageRange, 10, true);
    }

    @Test
    public void meetsAgeThreshold_NotMet() {
        AgeRange ageRange = AgeRange.of(13, 18);
        meetsAgeThreshold(ageRange, 21, false);
    }

    @Test
    public void meetsAgeThreshold_NotMet_Uncertain() {
        AgeRange ageRange = AgeRange.of(13, 18);
        meetsAgeThreshold(ageRange, 16, false);
    }

    @Test
    public void meetsAgeThreshold_Boundaries() {
        AgeRange ageRange = AgeRange.of(13, 18);
        meetsAgeThreshold(ageRange, 13, true);
        meetsAgeThreshold(ageRange, 14, false);
    }

    private void meetsAgeThreshold(AgeRange ageRange, int ageThreshold, boolean expectedIsMet) {
        boolean isMet = ageRange.meetsAgeThreshold(ageThreshold);
        assertThat(isMet).isEqualTo(expectedIsMet);
    }

    @Test
    public void serializeThenDeserialize_Of() {
        AgeRange ageRange = AgeRange.of(13, 18);
        serializeThenDeserialize(ageRange);
    }

    @Test
    public void serializeThenDeserialize_At() {
        AgeRange ageRange = AgeRange.at(18);
        serializeThenDeserialize(ageRange);
    }

    @Test
    public void serializeThenDeserialize_AtOrAbove() {
        AgeRange ageRange = AgeRange.atOrAbove(18);
        serializeThenDeserialize(ageRange);
    }

    @Test
    public void serializeThenDeserialize_Below() {
        AgeRange ageRange = AgeRange.below(13);
        serializeThenDeserialize(ageRange);
    }

    private void serializeThenDeserialize(AgeRange ageRange) {
        byte[] rawAgeRange = JsonValues.serialize(ageRange);
        AgeRange rtAgeRange = JsonValues.deserialize(rawAgeRange, new TypeReference<>() {});
        assertThat(rtAgeRange).isEqualTo(ageRange);
    }

    @Test
    public void equals() {
        new EqualsTester()
                .addEqualityGroup(AgeRange.of(13, 18), AgeRange.of(13, 18))
                .addEqualityGroup(AgeRange.atOrAbove(18))
                .addEqualityGroup(AgeRange.below(13))
                .testEquals();
    }

    @Test
    public void error_IllegalAgeRange_MinAgeBelowZero() {
        error_IllegalAgeRange(-1, 18, "min age must be at least 0: -1");
    }

    @Test
    public void error_IllegalAgeRange_MaxAgeNotGreaterThanMinAge() {
        error_IllegalAgeRange(18, 18, "max age must be greater than min age: [18, 18)");
    }

    private void error_IllegalAgeRange(int minAge, int maxAge, String expectedMessage) {
        assertThatThrownBy(() -> AgeRange.of(minAge, maxAge))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }
}
