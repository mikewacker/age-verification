package org.example.age.module.store.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.api.def.VerificationState;
import org.example.age.module.internal.resource.JsonResourceProvider;
import org.example.age.module.internal.resource.ResourceLoader;
import org.example.age.module.store.inmemory.VerificationStoreInitializer;
import org.example.age.service.store.VerificationStore;

/** {@link VerificationStoreInitializer} that gets accounts from a resource file. */
@Singleton
final class ResourceAvsVerificationStoreInitializer extends JsonResourceProvider<Map<String, VerificationState>>
        implements VerificationStoreInitializer {

    @Inject
    public ResourceAvsVerificationStoreInitializer(ResourceLoader resourceLoader, @Named("resourcesAvs") Path avsPath) {
        super(resourceLoader, avsPath.resolve("store/accounts.json"), new TypeReference<>() {});
    }

    @Override
    public void initialize(VerificationStore store) {
        Map<String, VerificationState> accounts = getInternal();
        accounts.forEach((accountId, state) -> store.trySave(accountId, state));
    }
}
