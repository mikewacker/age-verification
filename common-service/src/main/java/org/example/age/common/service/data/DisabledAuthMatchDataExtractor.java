package org.example.age.common.service.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.example.age.api.HttpOptional;
import org.example.age.common.api.data.AuthMatchData;
import org.example.age.common.api.data.AuthMatchDataExtractor;

/** {@link AuthMatchDataExtractor} that extracts {@link DisabledAuthMatchData}. */
@Singleton
final class DisabledAuthMatchDataExtractor extends AuthMatchDataExtractor {

    @Inject
    public DisabledAuthMatchDataExtractor(ObjectMapper mapper) {
        super(mapper, new TypeReference<DisabledAuthMatchData>() {});
    }

    @Override
    public HttpOptional<AuthMatchData> tryExtract(HttpServerExchange exchange) {
        return HttpOptional.of(DisabledAuthMatchData.of());
    }
}
