package org.example.age.module.crypto.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.age.testing.CompletionStageTesting.assertIsCompletedWithErrorCode;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.module.crypto.demo.testing.TestAgeCertificate;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.api.crypto.AvsVerifiedUserLocalizer;
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
    public void localize() throws Exception {
        VerifiedUser user = TestAgeCertificate.get().getUser();
        CompletionStage<VerifiedUser> localizedUserStage = userLocalizer.localize(user, "site");
        assertThat(localizedUserStage).isCompleted();
        VerifiedUser localizedUser = localizedUserStage.toCompletableFuture().get();
        assertThat(localizedUser).isNotEqualTo(user);
    }

    @Test
    public void error_UnregisteredSite() {
        VerifiedUser user = TestAgeCertificate.get().getUser();
        CompletionStage<VerifiedUser> localizedUserStage = userLocalizer.localize(user, "unregistered-site");
        assertIsCompletedWithErrorCode(localizedUserStage, 404);
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
