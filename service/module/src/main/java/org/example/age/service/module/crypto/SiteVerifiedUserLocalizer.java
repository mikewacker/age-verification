package org.example.age.service.module.crypto;

import java.util.concurrent.CompletionStage;
import org.example.age.common.api.VerifiedUser;

/** Localizes verified users. */
@FunctionalInterface
public interface SiteVerifiedUserLocalizer {

    CompletionStage<VerifiedUser> localize(VerifiedUser user);
}
