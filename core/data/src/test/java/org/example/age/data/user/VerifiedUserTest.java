package org.example.age.data.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import java.util.List;
import org.example.age.data.crypto.SecureId;
import org.junit.jupiter.api.Test;

public final class VerifiedUserTest {

    @Test
    public void localize() {
        VerifiedUser parent = VerifiedUser.of(SecureId.generate(), 40);
        VerifiedUser child = VerifiedUser.of(SecureId.generate(), 13, List.of(parent.pseudonym()));

        SecureId key = SecureId.generate();
        VerifiedUser localParent = parent.localize(key);
        VerifiedUser localChild = child.localize(key);
        assertThat(localParent.pseudonym()).isNotEqualTo(parent.pseudonym());
        assertThat(localParent.guardianPseudonyms()).isEmpty();
        assertThat(localChild.pseudonym()).isNotEqualTo(child.pseudonym());
        assertThat(localChild.guardianPseudonyms()).containsExactly(localParent.pseudonym());
    }

    @Test
    public void anonymizeAge() {
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 40);
        AgeThresholds ageThresholds = AgeThresholds.of(18);
        VerifiedUser anonymizedUser = user.anonymizeAge(ageThresholds);
        assertThat(anonymizedUser.ageRange()).isEqualTo(AgeRange.atOrAbove(18));
    }

    @Test
    public void serializeThenDeserialize() {
        JsonTester.serializeThenDeserialize(VerifiedUser.of(SecureId.generate(), 18), new TypeReference<>() {});
    }
}
