package org.example.age.avs.spi;

import jakarta.ws.rs.NotFoundException;
import java.util.concurrent.CompletionStage;
import org.example.age.common.api.VerifiedUser;

/** Localizes verified users for each site. Throws {@link NotFoundException} if the site is not registered. */
@FunctionalInterface
public interface AvsVerifiedUserLocalizer {

    CompletionStage<VerifiedUser> localize(VerifiedUser user, String siteId);
}
