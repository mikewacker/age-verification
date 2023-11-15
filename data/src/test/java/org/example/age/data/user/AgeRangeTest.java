package org.example.age.data.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.testing.EqualsTester;
import java.io.IOException;
import org.example.age.data.utils.DataMapper;
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
    public void meetsThreshold_Met() {
        AgeRange ageRange = AgeRange.of(13, 18);
        meetsThreshold(ageRange, 10, true);
    }

    @Test
    public void meetsThreshold_NotMet() {
        AgeRange ageRange = AgeRange.of(13, 18);
        meetsThreshold(ageRange, 21, false);
    }

    @Test
    public void meetsThreshold_NotMet_Uncertain() {
        AgeRange ageRange = AgeRange.of(13, 18);
        meetsThreshold(ageRange, 16, false);
    }

    @Test
    public void meetsThreshold_Boundaries() {
        AgeRange ageRange = AgeRange.of(13, 18);
        meetsThreshold(ageRange, 13, true);
        meetsThreshold(ageRange, 14, false);
    }

    private void meetsThreshold(AgeRange ageRange, int ageThreshold, boolean expectedIsMet) {
        boolean isMet = ageRange.meetsThreshold(ageThreshold);
        assertThat(isMet).isEqualTo(expectedIsMet);
    }

    @Test
    public void serializeThenDeserialize_Of() throws IOException {
        AgeRange ageRange = AgeRange.of(13, 18);
        serializeThenDeserialize(ageRange);
    }

    @Test
    public void serializeThenDeserialize_At() throws IOException {
        AgeRange ageRange = AgeRange.at(18);
        serializeThenDeserialize(ageRange);
    }

    @Test
    public void serializeThenDeserialize_AtOrAbove() throws IOException {
        AgeRange ageRange = AgeRange.atOrAbove(18);
        serializeThenDeserialize(ageRange);
    }

    @Test
    public void serializeThenDeserialize_Below() throws IOException {
        AgeRange ageRange = AgeRange.below(13);
        serializeThenDeserialize(ageRange);
    }

    private void serializeThenDeserialize(AgeRange ageRange) throws IOException {
        byte[] rawAgeRange = DataMapper.get().writeValueAsBytes(ageRange);
        AgeRange rtAgeRange = DataMapper.get().readValue(rawAgeRange, new TypeReference<>() {});
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