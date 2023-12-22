package org.example.age.module.key.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.data.crypto.SecureId;
import org.example.age.module.internal.resource.JsonResourceProvider;
import org.example.age.module.internal.resource.ResourceLoader;
import org.example.age.service.key.RefreshablePseudonymKeyProvider;

/** {@link RefreshablePseudonymKeyProvider} that gets the key from a resource file. Is not refreshable. */
@Singleton
final class ResourcePseudonymKeyProvider extends JsonResourceProvider<Map<String, SecureId>>
        implements RefreshablePseudonymKeyProvider {

    @Inject
    public ResourcePseudonymKeyProvider(ResourceLoader resourceLoader, @Named("resources") Path rootPath) {
        super(resourceLoader, rootPath.resolve("key/pseudonymKeys.json"), new TypeReference<>() {});
    }

    @Override
    public SecureId getPseudonymKey(String name) {
        Map<String, SecureId> pseudonymKeys = getInternal();
        SecureId pseudonymKey = pseudonymKeys.get(name);
        if (pseudonymKey == null) {
            throw new NoSuchElementException(name);
        }

        return pseudonymKey;
    }
}
