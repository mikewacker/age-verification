package org.example.age.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.List;
import org.example.age.api.crypto.SecureId;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class AgeThresholdsTest {

    @Test
    public void anonymize_Before() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        AgeRange ageRange = AgeRange.builder().min(12).max(12).build();
        AgeRange anonymizedAgeRange = ageThresholds.anonymize(ageRange);
        AgeRange expectedAgeRange = AgeRange.builder().max(12).build();
        assertThat(anonymizedAgeRange).isEqualTo(expectedAgeRange);
    }

    @Test
    public void anonymize_Between() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        AgeRange ageRange1 = AgeRange.builder().min(13).max(13).build();
        AgeRange anonymizedAgeRange1 = ageThresholds.anonymize(ageRange1);
        AgeRange expectedAgeRange = AgeRange.builder().min(13).max(17).build();
        assertThat(anonymizedAgeRange1).isEqualTo(expectedAgeRange);

        AgeRange ageRange2 = AgeRange.builder().min(17).max(17).build();
        AgeRange anonymizedAgeRange2 = ageThresholds.anonymize(ageRange2);
        assertThat(anonymizedAgeRange2).isEqualTo(expectedAgeRange);
    }

    @Test
    public void anonymize_After() {
        AgeThresholds ageThresholds = AgeThresholds.of(13, 18);
        AgeRange ageRange = AgeRange.builder().min(18).max(18).build();
        AgeRange anonymizedAgeRange = ageThresholds.anonymize(ageRange);
        AgeRange expectedAgeRange = AgeRange.builder().min(18).build();
        assertThat(anonymizedAgeRange).isEqualTo(expectedAgeRange);
    }

    @Test
    public void anonymize_User() {
        AgeThresholds ageThresholds = AgeThresholds.of(18);
        SecureId pseudonym1 = SecureId.generate();
        SecureId psuedonym2 = SecureId.generate();
        VerifiedUser user = VerifiedUser.builder()
                .pseudonym(pseudonym1)
                .ageRange(AgeRange.builder().min(18).max(18).build())
                .guardianPseudonyms(List.of(psuedonym2))
                .build();
        VerifiedUser anonymizedUser = ageThresholds.anonymize(user);
        VerifiedUser expectedUser = VerifiedUser.builder()
                .pseudonym(pseudonym1)
                .ageRange(AgeRange.builder().min(18).build())
                .guardianPseudonyms(List.of(psuedonym2))
                .build();
        assertThat(anonymizedUser).isEqualTo(expectedUser);
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        JsonTesting.serializeThenDeserialize(AgeThresholds.of(13, 18), AgeThresholds.class);
    }

    @Test
    public void error_EmptyThresholds() {
        assertThatThrownBy(() -> AgeThresholds.of())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("age thresholds must not be empty");
    }

    @Test
    public void error_ThresholdsDoNotIncrease() {
        assertThatThrownBy(() -> AgeThresholds.of(13, 13))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("age thresholds must increase");
    }
}
