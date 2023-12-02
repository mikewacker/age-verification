package org.example.age.common.service.crypto.internal;

import org.example.age.data.user.VerifiedUser;

/** Localizes a {@link VerifiedUser} using a pseudonym key. */
@FunctionalInterface
public interface VerifiedUserLocalizer {

    VerifiedUser localize(VerifiedUser user, String keyName);
}
