package org.example.age.module.store.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.Map;
import org.example.age.api.def.VerificationState;
import org.example.age.module.internal.resource.JsonResourceProvider;
import org.example.age.module.internal.resource.ResourceLoader;
import org.example.age.module.store.inmemory.VerificationStoreInitializer;
import org.example.age.service.store.VerificationStore;

/** {@link VerificationStoreInitializer} that gets accounts from a resource file. */
@Singleton
final class ResourceVerificationStoreInitializer extends JsonResourceProvider<Map<String, VerificationState>>
        implements VerificationStoreInitializer {

    @Inject
    public ResourceVerificationStoreInitializer(ResourceLoader resourceLoader, @Named("resources") Path rootPath) {
        super(resourceLoader, rootPath.resolve("store/accounts.json"), new TypeReference<>() {});
    }

    @Override
    public void initialize(VerificationStore store) {
        Map<String, VerificationState> accounts = getInternal();
        accounts.forEach((accountId, state) -> store.trySave(accountId, state));
    }
}
