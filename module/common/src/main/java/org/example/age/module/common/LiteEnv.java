package org.example.age.module.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.ExecutorService;

/** Lightweight environment that modules can depend on. */
public interface LiteEnv {

    /** Gets the JSON object mapper. */
    ObjectMapper jsonMapper();

    /** Gets the worker thread pool. */
    ExecutorService worker();

    /** Registers a provider with JAX-RS. */
    void registerProvider(Object provider);
}
