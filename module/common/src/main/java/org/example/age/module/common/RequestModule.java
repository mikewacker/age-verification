package org.example.age.module.common;

import dagger.Binds;
import dagger.Module;
import io.dropwizard.core.setup.Environment;

/**
 * Dagger module that binds {@link RequestContext}.
 * <p>
 * Depends on an unbound {@link Environment}.
 */
@Module
public interface RequestModule {

    @Binds
    RequestContext bindRequestContext(DropwizardRequestContext impl);
}
