package org.example.age.module.request.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.module.common.CommonModule;
import org.example.age.module.common.LiteEnv;
import org.example.age.module.common.RequestContextProvider;
import org.example.age.service.module.request.AccountIdContext;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AccountIdContext}
 *     <li>{@link RequestContextProvider}
 * </ul>
 * <p>
 * Depends on an unbound {@link LiteEnv}.
 * <p>
 * Uses an {@code Account-Id} header; it suffices to say that a production application should NOT do this.
 */
@Module(includes = CommonModule.class)
public interface DemoAccountIdModule {

    @Binds
    AccountIdContext bindAccountIdContext(DemoAccountIdContext impl);
}
