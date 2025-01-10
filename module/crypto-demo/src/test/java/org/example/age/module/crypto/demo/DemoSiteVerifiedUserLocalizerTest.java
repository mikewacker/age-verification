package org.example.age.module.crypto.demo;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import jakarta.inject.Singleton;
import java.util.concurrent.CompletionStage;
import org.example.age.api.VerifiedUser;
import org.example.age.module.crypto.demo.testing.TestDependenciesModule;
import org.example.age.service.module.crypto.SiteVerifiedUserLocalizer;
import org.example.age.testing.TestModels;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class DemoSiteVerifiedUserLocalizerTest {

    private static SiteVerifiedUserLocalizer userLocalizer;

    @BeforeAll
    public static void createVerifiedUserLocalizer() {
        TestComponent component = TestComponent.create();
        userLocalizer = component.verifiedUserLocalizer();
    }

    @Test
    public void localize() throws Exception {
        VerifiedUser user = TestModels.createVerifiedUser();
        CompletionStage<VerifiedUser> localizedUserStage = userLocalizer.localize(user);
        assertThat(localizedUserStage).isCompleted();
        VerifiedUser localizedUser = localizedUserStage.toCompletableFuture().get();
        assertThat(localizedUser).isNotEqualTo(user);
    }

    /** Dagger component for crypto. */
    @Component(modules = {DemoSiteCryptoModule.class, TestDependenciesModule.class})
    @Singleton
    interface TestComponent {

        static TestComponent create() {
            return DaggerDemoSiteVerifiedUserLocalizerTest_TestComponent.create();
        }

        SiteVerifiedUserLocalizer verifiedUserLocalizer();
    }
}
