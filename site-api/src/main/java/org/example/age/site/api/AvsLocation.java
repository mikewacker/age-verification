package org.example.age.site.api;

import com.google.common.net.HostAndPort;
import okhttp3.HttpUrl;
import org.example.age.data.crypto.SecureId;
import org.example.age.data.utils.DataStyle;
import org.immutables.value.Value;

/**
 * URL location of the age verification service.
 *
 * <p>A real implementation would use HTTPS.</p>
 */
@Value.Immutable
@DataStyle
public interface AvsLocation {

    /** Creates a builder for the location. */
    static Builder builder(String host, int port) {
        return builder(HostAndPort.fromParts(host, port));
    }

    /** Creates a builder for the location. */
    static Builder builder(HostAndPort hostAndPort) {
        return new Builder().hostAndPort(hostAndPort);
    }

    /** Host and port of the age verification service. */
    HostAndPort hostAndPort();

    /** Path of the API to create a verification session. */
    @Value.Default
    default String verificationSessionPath() {
        return "api/verification-session";
    }

    /** Path to redirect users to in order to continue age verification. */
    String redirectPath();

    /** URL of the API to create a verification session. */
    @Value.Derived
    default HttpUrl verificationSessionUrl(String siteId) {
        return new HttpUrl.Builder()
                .scheme("http")
                .host(hostAndPort().getHost())
                .port(hostAndPort().getPort())
                .addPathSegments(verificationSessionPath())
                .addQueryParameter("site-id", siteId)
                .build();
    }

    /** URL to redirect users to in order to continue age verification. */
    @Value.Derived
    default HttpUrl redirectUrl(SecureId requestId) {
        return new HttpUrl.Builder()
                .scheme("http")
                .host(hostAndPort().getHost())
                .port(hostAndPort().getPort())
                .addPathSegments(redirectPath())
                .addQueryParameter("request-id", requestId.toString())
                .build();
    }

    final class Builder extends ImmutableAvsLocation.Builder {}
}
