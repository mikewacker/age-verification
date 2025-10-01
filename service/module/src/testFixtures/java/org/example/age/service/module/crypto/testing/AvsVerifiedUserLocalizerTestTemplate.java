package org.example.age.service.module.crypto.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.util.WebStageTesting.await;
import static org.example.age.testing.util.WebStageTesting.awaitErrorCode;

import org.example.age.common.api.VerifiedUser;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;
import org.example.age.testing.api.TestModels;
import org.junit.jupiter.api.Test;

public abstract class AvsVerifiedUserLocalizerTestTemplate {

    @Test
    public void localize() {
        VerifiedUser user = TestModels.createVerifiedUser();
        VerifiedUser localizedUser = await(localizer().localize(user, "site1"));
        assertThat(localizedUser.getPseudonym()).isNotEqualTo(user.getPseudonym());
    }

    @Test
    public void localize_DifferentSites() {
        VerifiedUser user = TestModels.createVerifiedUser();
        VerifiedUser localizedUser1 = await(localizer().localize(user, "site1"));
        VerifiedUser localizedUser2 = await(localizer().localize(user, "site2"));
        assertThat(localizedUser1.getPseudonym()).isNotEqualTo(localizedUser2.getPseudonym());
    }

    @Test
    public void error_UnregisteredSite() {
        VerifiedUser user = TestModels.createVerifiedUser();
        awaitErrorCode(localizer().localize(user, "unregistered-site"), 404);
    }

    protected abstract AvsVerifiedUserLocalizer localizer();
}
