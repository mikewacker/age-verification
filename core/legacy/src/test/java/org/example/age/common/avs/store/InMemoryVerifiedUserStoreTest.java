package org.example.age.common.avs.store;

import static org.assertj.core.api.Assertions.assertThat;

import dagger.Component;
import java.util.Optional;
import javax.inject.Singleton;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.user.VerifiedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class InMemoryVerifiedUserStoreTest {

    private VerifiedUserStore userStore;

    @BeforeEach
    public void createVerifiedUserStore() {
        userStore = TestComponent.createVerifiedUserStore();
    }

    @Test
    public void saveAndLoad() {
        VerifiedUser user = createVerifiedUser();
        Optional<String> maybeDuplicateAccountId = userStore.trySave("name", user);
        assertThat(maybeDuplicateAccountId).isEmpty();
        assertThat(userStore.tryLoad("name")).hasValue(user);
    }

    @Test
    public void saveTwice() {
        VerifiedUser user = createVerifiedUser();
        userStore.trySave("name", user);
        assertThat(userStore.tryLoad("name")).hasValue(user);

        Optional<String> maybeDuplicateAccountId = userStore.trySave("name", user);
        assertThat(maybeDuplicateAccountId).isEmpty();
        assertThat(userStore.tryLoad("name")).hasValue(user);
    }

    @Test
    public void switchVerifiedAccount() {
        VerifiedUser user = createVerifiedUser();
        userStore.trySave("name1", user);
        assertThat(userStore.tryLoad("name1")).hasValue(user);

        userStore.delete("name1");
        Optional<String> maybeDuplicateAccountId = userStore.trySave("name2", user);
        assertThat(maybeDuplicateAccountId).isEmpty();
        assertThat(userStore.tryLoad("name1")).isEmpty();
        assertThat(userStore.tryLoad("name2")).hasValue(user);
    }

    @Test
    public void error_DuplicateVerification() {
        VerifiedUser user = createVerifiedUser();
        userStore.trySave("name1", user);
        assertThat(userStore.tryLoad("name1")).hasValue(user);

        Optional<String> maybeDuplicateAccountId = userStore.trySave("name2", user);
        assertThat(maybeDuplicateAccountId).hasValue("name1");
        assertThat(userStore.tryLoad("name1")).hasValue(user);
        assertThat(userStore.tryLoad("name2")).isEmpty();
    }

    private static VerifiedUser createVerifiedUser() {
        return VerifiedUser.of(SecureId.generate(), 18);
    }

    /** Dagger component that provides a {@link VerifiedUserStore}. */
    @Component(modules = InMemoryVerifiedUserStoreModule.class)
    @Singleton
    interface TestComponent {

        static VerifiedUserStore createVerifiedUserStore() {
            TestComponent component = DaggerInMemoryVerifiedUserStoreTest_TestComponent.create();
            return component.verifiedUserStore();
        }

        VerifiedUserStore verifiedUserStore();
    }
}
