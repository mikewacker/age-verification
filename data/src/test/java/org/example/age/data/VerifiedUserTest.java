package org.example.age.data;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.internal.SerializationUtils;
import org.junit.jupiter.api.Test;

public final class VerifiedUserTest {

    @Test
    public void localize() {
        SecureId parentPseudonym = SecureId.generate();
        SecureId childPseudonym = SecureId.generate();
        VerifiedUser parent = VerifiedUser.of(parentPseudonym, 40);
        VerifiedUser child = VerifiedUser.of(childPseudonym, 13, List.of(parentPseudonym));

        SecureId key = SecureId.generate();
        VerifiedUser localParent = parent.localize(key);
        VerifiedUser localChild = child.localize(key);
        assertThat(localParent.pseudonym()).isNotEqualTo(parent.pseudonym());
        assertThat(localParent.ageRange()).isEqualTo(parent.ageRange());
        assertThat(localParent.guardianPseudonyms()).isEmpty();
        assertThat(localChild.pseudonym()).isNotEqualTo(child.pseudonym());
        assertThat(localChild.ageRange()).isEqualTo(child.ageRange());
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
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        byte[] bytes = SerializationUtils.serialize(user);
        VerifiedUser deserializedUser = SerializationUtils.deserialize(bytes, VerifiedUser.class);
        assertThat(deserializedUser).isEqualTo(user);
    }
}
