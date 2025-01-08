package org.example.age.service.api.crypto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.age.api.AgeRange;
import org.example.age.api.VerifiedUser;
import org.example.age.api.crypto.SecureId;
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
        assertThat(localizedChild.getPseudonym()).isNotEqualTo(child.getPseudonym());
        assertThat(localizedChild.getGuardianPseudonyms()).containsExactly(localizedParent.getPseudonym());
    }
}
