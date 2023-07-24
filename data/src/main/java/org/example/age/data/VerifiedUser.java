package org.example.age.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.example.age.PackageImplementation;
import org.immutables.value.Value;

/**
 * User whose age and guardians (if applicable) are verified.
 *
 * <p>The only PII a {@link VerifiedUser} contains is an age, which can be anonymized into an age range.</p>
 */
@Value.Immutable
@PackageImplementation
@JsonSerialize(as = ImmutableVerifiedUser.class)
@JsonDeserialize(as = ImmutableVerifiedUser.class)
public interface VerifiedUser {

    /** Creates a verified user. */
    static VerifiedUser of(SecureId id, AgeRange ageRange, List<SecureId> guardianIds) {
        return ImmutableVerifiedUser.builder()
                .id(id)
                .ageRange(ageRange)
                .guardianIds(guardianIds)
                .build();
    }

    /** Creates a verified user without guardians. */
    static VerifiedUser of(SecureId id, int age) {
        return of(id, age, List.of());
    }

    /** Creates a verified user with guardians. */
    static VerifiedUser of(SecureId id, int age, List<SecureId> guardianIds) {
        AgeRange ageRange = AgeRange.at(age);
        return of(id, ageRange, guardianIds);
    }

    /** ID of the user. */
    SecureId id();

    /** Age range of the user. */
    AgeRange ageRange();

    /** IDs of the guardians, if the user is a minor. */
    List<SecureId> guardianIds();

    /** Produces a new set of IDs using the key. */
    default VerifiedUser localize(SecureId key) {
        SecureId localId = id().localize(key);
        List<SecureId> localGuardianIds =
                guardianIds().stream().map(id -> id.localize(key)).toList();
        return of(localId, ageRange(), localGuardianIds);
    }

    /** Anonymizes the age based on the age thresholds. */
    default VerifiedUser anonymizeAge(AgeThresholds ageThresholds) {
        AgeRange anonymizedAgeRange = ageThresholds.anonymize(ageRange());
        return of(id(), anonymizedAgeRange, guardianIds());
    }
}
