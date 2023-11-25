package org.example.age.common.service.data.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.infra.api.data.JsonSerializerModule;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataEncryptor}.
 *
 * <p>Depends on an unbound {@link ObjectMapper}.</p>
 */
@Module(includes = JsonSerializerModule.class)
public interface AuthMatchDataEncryptorModule {

    @Binds
    AuthMatchDataEncryptor bindAuthMatchDataEncryptor(AuthMatchDataEncryptorImpl impl);
}
