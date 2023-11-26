package org.example.age.test.avs.service;

import dagger.Binds;
import dagger.Module;
import java.security.PrivateKey;
import org.example.age.avs.api.AvsApi;
import org.example.age.avs.api.AvsApiModule;
import org.example.age.common.service.config.SiteLocation;
import org.example.age.common.service.crypto.internal.AuthMatchDataEncryptorModule;
import org.example.age.common.service.data.DisabledAuthMatchDataExtractorModule;
import org.example.age.common.service.data.internal.DataMapperModule;
import org.example.age.infra.service.client.RequestDispatcherModule;
import org.example.age.test.common.service.data.TestAccountIdExtractorModule;

/**
 * Dagger module that binds dependencies for <code>@Named("api") HttpHandler</code>.
 *
 * <p>Depends on an unbound...</p>
 * <ul>
 *     <li>{@link SiteLocation}</li>
 *     <li><code>@Named("signing") {@link PrivateKey}</code></li>
 * </ul>
 */
@Module(
        includes = {
            AvsApiModule.class,
            TestAccountIdExtractorModule.class,
            DisabledAuthMatchDataExtractorModule.class,
            AuthMatchDataEncryptorModule.class,
            RequestDispatcherModule.class,
            DataMapperModule.class,
        })
public interface FakeAvsServiceModule {

    @Binds
    AvsApi bindAvsApi(FakeAvsService service);
}
