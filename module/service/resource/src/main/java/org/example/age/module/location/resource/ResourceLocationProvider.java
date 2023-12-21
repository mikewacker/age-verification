package org.example.age.module.location.resource;

import com.fasterxml.jackson.core.type.TypeReference;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.example.age.module.internal.resource.DoubleCheckedProvider;
import org.example.age.module.internal.resource.ResourceLoader;
import org.example.age.service.location.Location;
import org.example.age.service.location.RefreshableAvsLocationProvider;
import org.example.age.service.location.RefreshableSiteLocationProvider;

/**
 * {@link RefreshableAvsLocationProvider} and {@link RefreshableSiteLocationProvider}
 * that gets locations from resource files. Is not refreshable.
 */
@Singleton
final class ResourceLocationProvider implements RefreshableSiteLocationProvider, RefreshableAvsLocationProvider {

    private final ResourceLoader resourceLoader;
    private final Path locationPath;

    private final Supplier<Locations> locationsProvider = DoubleCheckedProvider.create(this::loadLocations);

    @Inject
    public ResourceLocationProvider(
            ResourceLoader resourceLoader, @Named("resourcesLocation") Optional<Path> maybeLocationPath) {
        this.resourceLoader = resourceLoader;
        this.locationPath = maybeLocationPath.orElse(Path.of("location"));
    }

    @Override
    public Location getAvs() {
        return locationsProvider.get().avs();
    }

    @Override
    public Location getSite(String siteId) {
        Map<String, Location> siteLocations = locationsProvider.get().sites();
        Location siteLocation = siteLocations.get(siteId);
        if (siteLocation == null) {
            throw new NoSuchElementException(siteId);
        }

        return siteLocation;
    }

    /** Loads the {@link Locations}. */
    private Locations loadLocations() {
        Path path = locationPath.resolve("locations.json");
        return resourceLoader.loadJson(path, new TypeReference<>() {});
    }

    /** Locations for the age verification service and the sites. */
    private record Locations(Location avs, Map<String, Location> sites) {}
}
