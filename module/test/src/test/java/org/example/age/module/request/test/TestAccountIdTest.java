package org.example.age.module.request.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dagger.Component;
import jakarta.inject.Singleton;
import jakarta.ws.rs.NotAuthorizedException;
import java.util.function.Supplier;
import org.example.age.service.module.request.AccountIdContext;
import org.junit.jupiter.api.Test;

public final class TestAccountIdTest {

    private final AccountIdContext accountIdContext;
    private final TestAccountId accountId;

    public TestAccountIdTest() {
        TestComponent component = TestComponent.create();
        accountIdContext = component.get();
        accountId = component.accountId();
    }

    @Test
    public void accountId() {
        accountId.set("username");
        assertThat(accountIdContext.getForRequest()).isEqualTo("username");
    }

    @Test
    public void noAccountId() {
        assertThatThrownBy(accountIdContext::getForRequest).isInstanceOf(NotAuthorizedException.class);
    }

    /** Dagger component for {@link AccountIdContext} (and {@link TestAccountId}). */
    @Component(modules = TestRequestModule.class)
    @Singleton
    interface TestComponent extends Supplier<AccountIdContext> {

        static TestComponent create() {
            return DaggerTestAccountIdTest_TestComponent.create();
        }

        TestAccountId accountId();
    }
}
