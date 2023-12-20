package org.example.age.service.location.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.errorprone.annotations.FormatMethod;
import org.example.age.data.json.JsonStyle;
import org.immutables.value.Value;

/**
 * URL location of a server.
 *
 * <p>A real implementation would use HTTPS.</p>
 */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableLocation.class)
public interface Location {

    /** Creates a location. */
    static Location of(String host, int port) {
        return ImmutableLocation.builder().host(host).port(port).build();
    }

    /** Creates a location with a custom root path for the API endpoint. */
    static Location of(String host, int port, String apiRootPath) {
        return ImmutableLocation.builder()
                .host(host)
                .port(port)
                .apiRootPath(apiRootPath)
                .build();
    }

    /** Host of the server. */
    String host();

    /** Port of the server. */
    int port();

    /** Root path of the API endpoint. */
    @Value.Default
    default String apiRootPath() {
        return "/api";
    }

    /** Root URL of the server. */
    @Value.Derived
    @JsonIgnore
    default String rootUrl() {
        return String.format("http://%s:%d", host(), port());
    }

    /** URL at the specified path. */
    default String url(String path) {
        path = path.replaceFirst("^/", "");
        return String.format("%s/%s", rootUrl(), path);
    }

    /** URL at the specified path. */
    @FormatMethod
    default String url(String pathFormat, Object... args) {
        String path = String.format(pathFormat, args);
        return url(path);
    }

    /** URL for an API at the relative path. */
    default String apiUrl(String relativePath) {
        relativePath = relativePath.replaceFirst("^/", "");
        String path = String.format("%s/%s", apiRootPath(), relativePath);
        return url(path);
    }

    /** URL for an API at the relative path. */
    @FormatMethod
    default String apiUrl(String relativePathFormat, Object... args) {
        String relativePath = String.format(relativePathFormat, args);
        return apiUrl(relativePath);
    }
}
