package org.example.age.module.crypto.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.WebStageTesting.await;
import static org.example.age.testing.WebStageTesting.awaitErrorCode;

import dagger.Component;
import jakarta.inject.Singleton;
import org.example.age.api.VerifiedUser;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.module.crypto.AvsVerifiedUserLocalizer;
import org.example.age.testing.TestModels;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class DemoAvsVerifiedUserLocalizerTest {

    private static AvsVerifiedUserLocalizer userLocalizer;

    @BeforeAll
    public static void createVerifiedUserLocalizer() {
        TestComponent component = TestComponent.create();
        userLocalizer = component.verifiedUserLocalizer();
    }

    @Test
    public void localize() {
        VerifiedUser user = TestModels.createVerifiedUser();
        VerifiedUser localizedUser = await(userLocalizer.localize(user, "site"));
        assertThat(localizedUser).isNotEqualTo(user);
    }

    @Test
    public void error_UnregisteredSite() {
        VerifiedUser user = TestModels.createVerifiedUser();
        awaitErrorCode(userLocalizer.localize(user, "unregistered-site"), 404);
    }

    /** Dagger component for crypto. */
    @Component(modules = {DemoAvsCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerDemoAvsVerifiedUserLocalizerTest_TestComponent.create();
        }

        AvsVerifiedUserLocalizer verifiedUserLocalizer();
    }
}
