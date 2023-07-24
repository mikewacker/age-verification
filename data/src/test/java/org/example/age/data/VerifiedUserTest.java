package org.example.age.data;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class VerifiedUserTest {

    private static ObjectMapper mapper;

    @BeforeAll
    public static void createMapper() {
        mapper = new ObjectMapper();
        mapper.registerModule(new GuavaModule());
    }

    @Test
    public void localize() {
        SecureId parentId = SecureId.generate();
        SecureId childId = SecureId.generate();
        VerifiedUser parent = VerifiedUser.of(parentId, 40);
        VerifiedUser child = VerifiedUser.of(childId, 13, List.of(parentId));

        SecureId key = SecureId.generate();
        VerifiedUser localParent = parent.localize(key);
        VerifiedUser localChild = child.localize(key);
        assertThat(localParent.id()).isNotEqualTo(parent.id());
        assertThat(localParent.ageRange()).isEqualTo(parent.ageRange());
        assertThat(localParent.guardianIds()).isEmpty();
        assertThat(localChild.id()).isNotEqualTo(child.id());
        assertThat(localChild.ageRange()).isEqualTo(child.ageRange());
        assertThat(localChild.guardianIds()).containsExactly(localParent.id());
    }

    @Test
    public void anonymizeAge() {
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 40);
        AgeThresholds ageThresholds = AgeThresholds.of(18);
        VerifiedUser anonymizedUser = user.anonymizeAge(ageThresholds);
        assertThat(anonymizedUser.ageRange()).isEqualTo(AgeRange.atOrAbove(18));
    }

    @Test
    public void serializeThenDeserialize() throws JsonProcessingException {
        VerifiedUser user = VerifiedUser.of(SecureId.generate(), 18);
        String json = mapper.writeValueAsString(user);
        VerifiedUser deserializedUser = mapper.readValue(json, VerifiedUser.class);
        assertThat(deserializedUser).isEqualTo(user);
    }
}
