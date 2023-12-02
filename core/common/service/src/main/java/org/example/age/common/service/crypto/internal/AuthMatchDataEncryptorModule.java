package org.example.age.common.service.crypto.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Binds;
import dagger.Module;
import org.example.age.infra.service.ServiceJsonSerializerModule;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataEncryptor}.
 *
 * <p>Depends on an unbound <code>@Named("service") {@link ObjectMapper}</code>.</p>
 */
@Module(includes = ServiceJsonSerializerModule.class)
public interface AuthMatchDataEncryptorModule {

    @Binds
    AuthMatchDataEncryptor bindAuthMatchDataEncryptor(AuthMatchDataEncryptorImpl impl);
}
