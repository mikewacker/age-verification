package org.example.age.service.api;

import java.util.concurrent.CompletionStage;
import org.example.age.api.crypto.SecureId;

/** Stores the key used to localize users. */
@FunctionalInterface
public interface SiteLocalizationKeyStore {

    /** Gets the key used to localize users. */
    CompletionStage<SecureId> get();
}
