package org.example.age.common.api.data;

import io.undertow.server.HttpServerExchange;
import java.util.Optional;
import org.example.age.api.Sender;
import org.example.age.data.crypto.Aes256Key;
import org.example.age.data.crypto.AesGcmEncryptionPackage;

/**
 * Extracts {@link AuthMatchData} from an {@link HttpServerExchange} or an {@link AesGcmEncryptionPackage},
 * or sends an error status code.
 */
public interface AuthMatchDataExtractor {

    Optional<AuthMatchData> tryExtract(HttpServerExchange exchange, Sender sender);

    Optional<AuthMatchData> tryDecrypt(AesGcmEncryptionPackage token, Aes256Key key, Sender sender);
}
