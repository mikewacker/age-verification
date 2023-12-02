package org.example.age.common.service.data;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
        return "/api/verification-session";
    }

    /** Path to redirect users to in order to continue age verification. */
    String redirectPath();

    /** URL of the API to create a verification session. */
    default String verificationSessionUrl(String siteId) {
        String path = verificationSessionPath().replaceFirst("^/", "");
        return String.format("http://%s:%d/%s?site-id=%s", host(), port(), path, siteId);
    }

    /** URL to redirect users to in order to continue age verification. */
    default String redirectUrl(SecureId requestId) {
        String path = redirectPath().replaceFirst("^/", "");
        return String.format("http://%s:%d/%s?request-id=%s", host(), port(), path, requestId);
    }

    final class Builder extends ImmutableAvsLocation.Builder {}
}
