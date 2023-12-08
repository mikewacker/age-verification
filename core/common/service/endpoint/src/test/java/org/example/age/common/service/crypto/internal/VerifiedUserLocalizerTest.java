package org.example.age.common.service.crypto.internal;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import javax.inject.Singleton;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.example.age.module.key.common.test.TestKeyModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class VerifiedUserLocalizerTest {

    private static VerifiedUserLocalizer userLocalizer;

    @BeforeAll
    public static void createVerifiedUserLocalizer() {
        userLocalizer = TestComponent.createVerifiedUserLocalizer();
    }

    @Test
    public void localize() {
        VerifiedUser user = createVerifiedUser();
        VerifiedUser localUser = userLocalizer.localize(user, "local");
        assertThat(localUser).isNotEqualTo(user);
    }

    private static VerifiedUser createVerifiedUser() {
        return VerifiedUser.of(SecureId.generate(), 18);
    }

    /** Dagger component that provides a {@link VerifiedUserLocalizer}. */
    @Component(modules = {VerifiedUserLocalizerModule.class, TestKeyModule.class})
    @Singleton
    interface TestComponent {

        static VerifiedUserLocalizer createVerifiedUserLocalizer() {
            TestComponent component = DaggerVerifiedUserLocalizerTest_TestComponent.create();
            return component.verifiedUserLocalizer();
        }

        VerifiedUserLocalizer verifiedUserLocalizer();
    }
}
