package org.example.age.testing.site.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;

import java.util.Optional;
import org.example.age.avs.spi.AvsVerifiedUserStore;
import org.example.age.common.api.VerifiedUser;
import org.junit.jupiter.api.Test;

public abstract class AvsAccountStoreTestTemplate {

    @Test
    public void load_Present() {
        Optional<VerifiedUser> maybeUser = await(store().tryLoad("person"));
        assertThat(maybeUser).isPresent();
    }

    @Test
    public void load_Empty() {
        Optional<VerifiedUser> maybeUser = await(store().tryLoad("unverified-person"));
        assertThat(maybeUser).isEmpty();
    }

    protected abstract AvsVerifiedUserStore store();
}
