package org.example.age.common.provider.account.demo;

import dagger.Binds;
import dagger.Module;
import io.github.mikewacker.darc.RequestContextModule;
import org.example.age.common.spi.AccountIdContext;

/**
 * Dagger module that binds {@link AccountIdContext}.
 * <p>
 * Depends on an unbound {@code Environment}.
 * <p>
 * Uses an {@code Account-Id} header; it suffices to say that a production application should NOT do this.
 */
@Module(includes = RequestContextModule.class)
public interface DemoAccountIdModule {

    @Binds
    AccountIdContext bindAccountIdContext(DemoAccountIdContext impl);
}
