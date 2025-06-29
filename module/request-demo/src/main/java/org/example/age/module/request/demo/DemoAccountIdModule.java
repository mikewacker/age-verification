package org.example.age.module.request.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.common.RequestModule;
import org.example.age.service.module.request.AccountIdContext;

/**
 * Dagger module that binds {@link AccountIdContext}.
 * <p>
 * Depends on an unbound {@code Environment}.
 * <p>
 * Uses an {@code Account-Id} header; it suffices to say that a production application should NOT do this.
 */
@Module(includes = RequestModule.class)
public interface DemoAccountIdModule {

    @Binds
    AccountIdContext bindAccountIdContext(DemoAccountIdContext impl);
}
