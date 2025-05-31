package org.example.age.service.module.crypto.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.common.testing.WebStageTesting.await;
import static org.example.age.common.testing.WebStageTesting.awaitErrorCode;

import org.example.age.api.VerifiedUser;
import org.example.age.api.testing.TestModels;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;
import org.junit.jupiter.api.Test;

public abstract class AvsVerifiedUserLocalizerTestTemplate {

    @Test
    public void localize() {
        VerifiedUser user = TestModels.createVerifiedUser();
        VerifiedUser localizedUser = await(localizer().localize(user, "site"));
        assertThat(localizedUser).isNotEqualTo(user);
    }

    @Test
    public void error_UnregisteredSite() {
        VerifiedUser user = TestModels.createVerifiedUser();
        awaitErrorCode(localizer().localize(user, "unregistered-site"), 404);
    }

    protected abstract AvsVerifiedUserLocalizer localizer();
}
