package org.example.age.common.api.data.auth;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import org.example.age.api.Sender;
import org.example.age.data.certificate.AuthKey;
import org.example.age.data.certificate.AuthToken;

/**
 * Extracts {@link AuthMatchData} from an {@link HttpServerExchange} or an {@link AuthToken},
 * or sends an error status code.
 */
public interface AuthMatchDataExtractor {

    Optional<AuthMatchData> tryExtract(HttpServerExchange exchange, Sender sender);

    Optional<AuthMatchData> tryDecrypt(AuthToken token, AuthKey key, Sender sender);
}
