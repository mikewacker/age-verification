package org.example.age.common.api.crypto;

import java.util.List;
import org.example.age.common.api.VerifiedUser;

/** Localizes verified users using a localization key. */
public final class Localization {

    /** Localizes a {@link VerifiedUser} using the key. */
    public static VerifiedUser localize(VerifiedUser user, SecureId key) {
        SecureId localizedPseudonym = user.getPseudonym().localize(key);
        List<SecureId> localizedGuardianPseudonyms = user.getGuardianPseudonyms().stream()
                .map(id -> id.localize(key))
                .toList();
        return VerifiedUser.builder()
                .pseudonym(localizedPseudonym)
                .ageRange(user.getAgeRange())
                .guardianPseudonyms(localizedGuardianPseudonyms)
                .build();
    }

    // static class
    private Localization() {}
}
