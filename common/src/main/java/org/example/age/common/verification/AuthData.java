package org.example.age.common.verification;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.nio.charset.StandardCharsets;

/**
 * Authentication data that is derived from an {@link HttpServerExchange}.
 *
 * <p>This authentication data is used to ensure that the person who locally requested to verify their age on a site
 * is the same as the person who remotely requested an age certificate from the age verification service.</p>
 *
 * <p>Authentication is done on a best-effort basis, subject to tradeoffs involving privacy, false negatives, etc.
 * The goal is to increase the accuracy of the system, not to make it impossible to bypass the system.</p>
 *
 * <p>For the proof-of-concept, we only match the {@code User-Agent} header;
 * a real implementation would likely do something more sophisticated than this.</p>
 */
public final class AuthData {

    private final String userAgent;

    /** Creates authentication data from an {@link HttpServerExchange}. */
    public static AuthData create(HttpServerExchange exchange) {
        String userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
        userAgent = (userAgent != null) ? userAgent : "";
        return new AuthData(userAgent);
    }

    /** Deserializes the data from raw bytes. */
    public static AuthData deserialize(byte[] bytes) {
        String userAgent = new String(bytes, StandardCharsets.UTF_8);
        return new AuthData(userAgent);
    }

    /** Compares two sets of authentication data, determining if they came from the same person. */
    public boolean match(AuthData other) {
        return userAgent.equals(other.userAgent);
    }

    /** Serializes the data to raw bytes. */
    public byte[] serialize() {
        return userAgent.getBytes(StandardCharsets.UTF_8);
    }

    private AuthData(String userAgent) {
        this.userAgent = userAgent;
    }
}
