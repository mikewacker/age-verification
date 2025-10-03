package org.example.age.service.module.crypto.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;

import org.example.age.common.api.VerifiedUser;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;
import org.example.age.testing.api.TestModels;
import org.junit.jupiter.api.Test;

public abstract class SiteVerifiedUserLocalizerTestTemplate {

    @Test
    public void localize() {
        VerifiedUser user = TestModels.createVerifiedUser();
        VerifiedUser localizedUser = await(localizer().localize(user));
        assertThat(localizedUser).isNotEqualTo(user);
    }

    protected abstract SiteVerifiedUserLocalizer localizer();
}
