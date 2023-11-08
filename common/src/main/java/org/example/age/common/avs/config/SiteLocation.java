package org.example.age.common.avs.config;

import com.google.common.net.HostAndPort;
import okhttp3.HttpUrl;
import org.example.age.data.DataStyle;
import org.immutables.value.Value;

/**
 * URL location of a site.
 *
 * <p>A real implementation would used HTTPS.</p>
 */
@Value.Immutable
@DataStyle
public interface SiteLocation {

    /** Creates a builder for the location. */
    static Builder builder(String host, int port) {
        return builder(HostAndPort.fromParts(host, port));
    }

    /** Creates a builder for the location. */
    static Builder builder(HostAndPort hostAndPort) {
        return new Builder().hostAndPort(hostAndPort);
    }

    /** Host and port of the site. */
    HostAndPort hostAndPort();

    /** Path of the API to process an age certificate. */
    @Value.Default
    default String ageCertificatePath() {
        return "api/age-certificate";
    }

    /** Path to redirect users to in order to continue age verification. */
    String redirectPath();

    /** URL of the API to process an age certificate. */
    @Value.Derived
    default HttpUrl ageCertificateUrl() {
        return new HttpUrl.Builder()
                .scheme("http")
                .host(hostAndPort().getHost())
                .port(hostAndPort().getPort())
                .addPathSegments(ageCertificatePath())
                .build();
    }

    /** URL to redirect users to in order to continue age verification. */
    @Value.Derived
    default HttpUrl redirectUrl() {
        return new HttpUrl.Builder()
                .scheme("http")
                .host(hostAndPort().getHost())
                .port(hostAndPort().getPort())
                .addPathSegments(redirectPath())
                .build();
    }

    final class Builder extends ImmutableSiteLocation.Builder {}
}