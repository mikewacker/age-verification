package org.example.age.common.service.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.Sender;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;

/** {@link AuthMatchDataExtractor} that extracts {@link UserAgentAuthMatchData}. */
@Singleton
final class UserAgentAuthMatchDataExtractor extends AuthMatchDataExtractor {

    @Inject
    public UserAgentAuthMatchDataExtractor(ObjectMapper mapper) {
        super(mapper, new TypeReference<UserAgentAuthMatchData>() {});
    }

    @Override
    public Optional<AuthMatchData> tryExtract(HttpServerExchange exchange, Sender sender) {
        String userAgent = exchange.getRequestHeaders().getFirst(Headers.USER_AGENT);
        userAgent = (userAgent != null) ? userAgent : "";
        AuthMatchData data = UserAgentAuthMatchData.of(userAgent);
        return Optional.of(data);
    }
}
