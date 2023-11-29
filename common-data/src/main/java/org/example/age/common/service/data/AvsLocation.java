package org.example.age.common.service.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@JsonSerialize(as = ImmutableAvsLocation.class)
@JsonDeserialize(as = ImmutableAvsLocation.class)
public interface AvsLocation {

    /** Creates a builder for the location. */
    static Builder builder(String host, int port) {
        return new Builder().host(host).port(port);
    }

    /** Host of the age verification service. */
    String host();

    /** Port of the age verification service. */
    int port();

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
                .host(host())
                .port(port())
                .addPathSegments(verificationSessionPath())
                .addQueryParameter("site-id", siteId)
                .build();
    }

    /** URL to redirect users to in order to continue age verification. */
    @Value.Derived
    default HttpUrl redirectUrl(SecureId requestId) {
        return new HttpUrl.Builder()
                .scheme("http")
                .host(host())
                .port(port())
                .addPathSegments(redirectPath())
                .addQueryParameter("request-id", requestId.toString())
                .build();
    }

    final class Builder extends ImmutableAvsLocation.Builder {}
}
