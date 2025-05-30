package org.example.age.module.common.testing;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/** Extension that cleans a test container and provides a client to access it. */
public abstract class BaseTestContainer<C> implements BeforeAllCallback, AfterAllCallback {

    private C client;

    /** Gets the client. */
    public final C getClient() {
        if (client == null) {
            throw new IllegalStateException("not initialized (missing @RegisterExtension?)");
        }

        return client;
    }

    @Override
    public final void beforeAll(ExtensionContext context) throws Exception {
        client = createClient();
        clean(client);
    }

    @Override
    public final void afterAll(ExtensionContext context) throws Exception {
        clean(client);
        closeClient(client);
    }

    /** Creates the client. */
    protected abstract C createClient() throws Exception;

    /** Cleans the test container. */
    protected abstract void clean(C client) throws Exception;

    /** Closes the client. */
    protected abstract void closeClient(C client) throws Exception;
}
