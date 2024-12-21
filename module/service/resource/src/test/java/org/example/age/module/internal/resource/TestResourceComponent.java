package org.example.age.module.internal.resource;

import dagger.Component;
import jakarta.inject.Singleton;

/** Dagger component that provides a {@link ResourceLoader}. */
@Component(modules = {ResourceLoaderModule.class, TestSiteResourceModule.class}) // Either Site or Avs works.
@Singleton
public interface TestResourceComponent {

    static ResourceLoader createResourceLoader() {
        TestResourceComponent component = DaggerTestResourceComponent.create();
        return component.resourceLoader();
    }

    ResourceLoader resourceLoader();
}
