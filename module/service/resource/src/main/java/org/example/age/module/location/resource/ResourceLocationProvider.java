package org.example.age.module.location.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.module.internal.resource.JsonResourceProvider;
import org.example.age.module.internal.resource.ResourceLoader;
import org.example.age.service.location.Location;
import org.example.age.service.location.RefreshableAvsLocationProvider;
import org.example.age.service.location.RefreshableSiteLocationProvider;

/**
 * {@link RefreshableAvsLocationProvider} and {@link RefreshableSiteLocationProvider}
 * that gets locations from a resource file. Is not refreshable.
 */
@Singleton
final class ResourceLocationProvider extends JsonResourceProvider<ResourceLocationProvider.Locations>
        implements RefreshableSiteLocationProvider, RefreshableAvsLocationProvider {

    @Inject
    public ResourceLocationProvider(
            ResourceLoader resourceLoader, @Named("resourcesLocation") Optional<Path> maybeLocationPath) {
        super(
                resourceLoader,
                maybeLocationPath.orElse(Path.of("location")).resolve("locations.json"),
                new TypeReference<>() {});
    }

    @Override
    public Location getAvs() {
        return getInternal().avs();
    }

    @Override
    public Location getSite(String siteId) {
        Map<String, Location> siteLocations = getInternal().sites();
        Location siteLocation = siteLocations.get(siteId);
        if (siteLocation == null) {
            throw new NoSuchElementException(siteId);
        }

        return siteLocation;
    }

    /** Locations for the age verification service and the sites. */
    public record Locations(Location avs, Map<String, Location> sites) {}
}
