package org.example.age.module.request.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.module.request.AccountIdContext;
import org.example.age.service.module.request.RequestContextModule;
import org.example.age.service.module.request.RequestContextProvider;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AccountIdContext}
 *     <li>{@link RequestContextProvider}
 * </ul>
 * <p>
 * Uses an {@code Account-Id} header; it suffices to say that a production application should NOT do this.
 */
@Module(includes = RequestContextModule.class)
public interface DemoAccountIdModule {

    @Binds
    AccountIdContext bindAccountIdContext(DemoAccountIdContext impl);
}
