package org.example.age.module.request.demo;

import dagger.Binds;
import dagger.Module;
import org.example.age.service.RequestContextModule;
import org.example.age.service.RequestContextProvider;
import org.example.age.service.api.request.AccountIdContext;

/**
 * Dagger module that binds...
 * <ul>
 *     <li>{@link AccountIdContext}
 *     <li>{@link RequestContextProvider}
 * </ul>
 * <p>
 * Uses an {@code Account-Id} header.
 */
@Module(includes = RequestContextModule.class)
public interface DemoAccountIdModule {

    @Binds
    AccountIdContext bindAccountIdContext(DemoAccountIdContext impl);
}
