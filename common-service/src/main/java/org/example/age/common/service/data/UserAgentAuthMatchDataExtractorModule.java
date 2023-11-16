package org.example.age.common.service.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.common.api.data.AuthMatchDataExtractor;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataExtractor},
 * which matches the {@code User-Agent} header.
 *
 * <p>Depends on an unbound {@link ObjectMapper}.</p>
 */
@Module
public interface UserAgentAuthMatchDataExtractorModule {

    @Binds
    AuthMatchDataExtractor bindAuthMatchDataExtractor(UserAgentAuthMatchDataExtractor impl);
}
