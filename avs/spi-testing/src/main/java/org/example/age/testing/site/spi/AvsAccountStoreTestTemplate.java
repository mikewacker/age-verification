package org.example.age.testing.site.spi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.client.WebStageTesting.await;
import static org.example.age.testing.client.WebStageTesting.awaitErrorCode;

import org.example.age.avs.spi.AvsVerifiedAccountStore;
import org.example.age.avs.spi.VerifiedAccount;
import org.junit.jupiter.api.Test;

public abstract class AvsAccountStoreTestTemplate {

    @Test
    public void load() {
        VerifiedAccount account = await(store().load("person"));
        assertThat(account.id()).isEqualTo("person");
    }

    @Test
    public void error_UnverifiedAccount() {
        awaitErrorCode(store().load("unverified-person"), 403);
    }

    protected abstract AvsVerifiedAccountStore store();
}
