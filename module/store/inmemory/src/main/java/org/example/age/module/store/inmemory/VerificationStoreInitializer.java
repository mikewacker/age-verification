package org.example.age.module.store.inmemory;

import org.example.age.service.store.VerificationStore;

/** Initializes a {@link VerificationStore}. */
@FunctionalInterface
public interface VerificationStoreInitializer {

    void initialize(VerificationStore store);
}
