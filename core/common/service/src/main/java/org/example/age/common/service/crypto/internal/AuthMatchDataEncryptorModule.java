package org.example.age.common.service.crypto.internal;

import dagger.Binds;
import dagger.Module;
import org.example.age.api.JsonSerializer;

/**
 * Dagger module that publishes a binding for {@link AuthMatchDataEncryptor}.
 *
 * <p>Depends on an unbound <code>@Named("service") {@link JsonSerializer}</code>.</p>
 */
@Module
public interface AuthMatchDataEncryptorModule {

    @Binds
    AuthMatchDataEncryptor bindAuthMatchDataEncryptor(AuthMatchDataEncryptorImpl impl);
}
