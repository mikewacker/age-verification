package org.example.age.module.location.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.age.data.json.JsonStyle;
import org.immutables.value.Value;

/**
 * URL location of a site.
 *
 * <p>A real implementation would used HTTPS.</p>
 */
@Value.Immutable
@JsonStyle
@JsonDeserialize(as = ImmutableSiteLocation.class)
public interface SiteLocation {

    /** Creates a builder for the location. */
    static SiteLocation.Builder builder(String host, int port) {
        return new Builder().host(host).port(port);
    }

    /** Host of the site. */
    String host();

    /** Port of the site. */
    int port();

    /** Path of the API to process an age certificate. */
    @Value.Default
    default String ageCertificatePath() {
        return "/api/age-certificate";
    }

    /** Path to redirect users to in order to continue age verification. */
    String redirectPath();

    /** URL of the API to process an age certificate. */
    @Value.Derived
    @JsonIgnore
    default String ageCertificateUrl() {
        String path = ageCertificatePath().replaceFirst("^/", "");
        return String.format("http://%s:%d/%s", host(), port(), path);
    }

    /** URL to redirect users to in order to continue age verification. */
    @Value.Derived
    @JsonIgnore
    default String redirectUrl() {
        String path = redirectPath().replaceFirst("^/", "");
        return String.format("http://%s:%d/%s", host(), port(), path);
    }

    final class Builder extends ImmutableSiteLocation.Builder {}
}
