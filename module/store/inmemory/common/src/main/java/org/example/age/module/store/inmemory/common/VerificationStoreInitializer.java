package org.example.age.module.store.inmemory.common;

import org.example.age.service.store.common.VerificationStore;

/** Initializes a {@link VerificationStore}. */
@FunctionalInterface
public interface VerificationStoreInitializer {

    void initialize(VerificationStore store);
}
