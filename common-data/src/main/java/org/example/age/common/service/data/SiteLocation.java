package org.example.age.common.service.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import okhttp3.HttpUrl;
import org.example.age.data.utils.DataStyle;
import org.immutables.value.Value;

/**
 * URL location of a site.
 *
 * <p>A real implementation would used HTTPS.</p>
 */
@Value.Immutable
@DataStyle
@JsonSerialize(as = ImmutableSiteLocation.class)
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
        return "api/age-certificate";
    }

    /** Path to redirect users to in order to continue age verification. */
    String redirectPath();

    /** URL of the API to process an age certificate. */
    @Value.Derived
    @JsonIgnore
    default HttpUrl ageCertificateUrl() {
        return new HttpUrl.Builder()
                .scheme("http")
                .host(host())
                .port(port())
                .addPathSegments(ageCertificatePath())
                .build();
    }

    /** URL to redirect users to in order to continue age verification. */
    @Value.Derived
    @JsonIgnore
    default HttpUrl redirectUrl() {
        return new HttpUrl.Builder()
                .scheme("http")
                .host(host())
                .port(port())
                .addPathSegments(redirectPath())
                .build();
    }

    final class Builder extends ImmutableSiteLocation.Builder {}
}
