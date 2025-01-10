package org.example.age.service.module.crypto;

import jakarta.ws.rs.NotFoundException;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;

/** Localizes verified users for each site. Throws {@link NotFoundException} if the site is not registered. */
@FunctionalInterface
public interface AvsVerifiedUserLocalizer {

    CompletionStage<VerifiedUser> localize(VerifiedUser user, String siteId);
}
