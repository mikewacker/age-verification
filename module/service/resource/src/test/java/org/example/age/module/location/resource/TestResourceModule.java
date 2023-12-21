package org.example.age.module.location.resource;

import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

/** Dagger module that publishes a binding for <code>@Named("resources") {@link Class}&lt;?&gt;</code>. */
@Module
public interface TestResourceModule {

    @Provides
    @Named("resources")
    @Singleton
    static Class<?> provideResourcesClass() {
        return TestResourceModule.class;
    }
}
