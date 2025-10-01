package org.example.age.common.api.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.VerifiedUser;
import org.junit.jupiter.api.Test;

public final class LocalizationTest {

    @Test
    public void localize() {
        VerifiedUser parent = VerifiedUser.builder()
                .pseudonym(SecureId.generate())
                .ageRange(AgeRange.builder().min(18).build())
                .build();
        VerifiedUser child = VerifiedUser.builder()
                .pseudonym(SecureId.generate())
                .ageRange(AgeRange.builder().min(13).max(17).build())
                .guardianPseudonyms(List.of(parent.getPseudonym()))
                .build();

        SecureId key = SecureId.generate();
        VerifiedUser localizedParent = Localization.localize(parent, key);
        VerifiedUser localizedChild = Localization.localize(child, key);

        assertThat(localizedParent.getPseudonym()).isNotEqualTo(parent.getPseudonym());
        assertThat(localizedParent.getAgeRange()).isEqualTo(parent.getAgeRange());
        assertThat(localizedParent.getGuardianPseudonyms()).isEmpty();

        assertThat(localizedChild.getPseudonym()).isNotEqualTo(child.getPseudonym());
        assertThat(localizedChild.getAgeRange()).isEqualTo(child.getAgeRange());
        assertThat(localizedChild.getGuardianPseudonyms()).containsExactly(localizedParent.getPseudonym());
    }
}
