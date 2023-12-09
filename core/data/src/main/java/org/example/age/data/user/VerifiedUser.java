package org.example.age.data.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.json.JsonStyle;
import org.immutables.value.Value;

/**
 * User whose age and guardians (if applicable) are verified.
 *
 * <p>The only PII a {@link VerifiedUser} contains is an age, which can be anonymized into an age range.</p>
 */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableVerifiedUser.class)
public interface VerifiedUser {

    /** Creates a verified user. */
    static VerifiedUser of(SecureId pseudonym, AgeRange ageRange, List<SecureId> guardianPseudonyms) {
        return ImmutableVerifiedUser.builder()
                .pseudonym(pseudonym)
                .ageRange(ageRange)
                .guardianPseudonyms(guardianPseudonyms)
                .build();
    }

    /** Creates a verified user without guardians. */
    static VerifiedUser of(SecureId pseudonym, int age) {
        return of(pseudonym, age, List.of());
    }

    /** Creates a verified user with guardians. */
    static VerifiedUser of(SecureId pseudonym, int age, List<SecureId> guardianPseudonyms) {
        AgeRange ageRange = AgeRange.at(age);
        return of(pseudonym, ageRange, guardianPseudonyms);
    }

    /** Pseudonym to identify the user. */
    SecureId pseudonym();

    /** Age range of the user. */
    AgeRange ageRange();

    /** Pseudonyms of the guardians, if the user is a minor. */
    List<SecureId> guardianPseudonyms();

    /** Changes the pseudonym using the key. */
    default VerifiedUser localize(SecureId key) {
        SecureId localPseudonym = pseudonym().localize(key);
        List<SecureId> localGuardianPseudonyms =
                guardianPseudonyms().stream().map(id -> id.localize(key)).toList();
        return of(localPseudonym, ageRange(), localGuardianPseudonyms);
    }

    /** Anonymizes the age based on the age thresholds. */
    default VerifiedUser anonymizeAge(AgeThresholds ageThresholds) {
        AgeRange anonymizedAgeRange = ageThresholds.anonymize(ageRange());
        return of(pseudonym(), anonymizedAgeRange, guardianPseudonyms());
    }
}
