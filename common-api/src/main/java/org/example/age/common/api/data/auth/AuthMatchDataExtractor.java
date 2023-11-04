package org.example.age.common.api.data.auth;

import io.undertow.server.HttpServerExchange;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;

/** Extracts {@link AuthMatchData} from an {@link HttpServerExchange}, or decrypts it from an {@link AuthToken}. */
public interface AuthMatchDataExtractor {

    /** Extracts {@link AuthMatchData} from an {@link HttpServerExchange}. */
    AuthMatchData extract(HttpServerExchange exchange);

    /**
     * Decrypts {@link AuthMatchData} from an {@link AuthToken}.
     *
     * <p>If the decrypted bytes cannot be deserialized into {@link AuthMatchData}, an exception can be thrown.</p>
     */
    AuthMatchData decrypt(AuthToken token, AuthKey key);
}
