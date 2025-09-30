package org.example.age.module.common.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public final class BaseTestContainerTest {

    @Test
    public void testLifecycle() throws Exception {
        FakeTestContainer container = new FakeTestContainer();
        container.beforeAll(null);
        FakeClient client = container.getClient();
        assertThat(client.isClean()).isTrue();

        client.doOps();
        container.afterAll(null);
        assertThat(client.isClean()).isTrue();
        assertThat(client.isClosed()).isTrue();
    }

    @Test
    public void error_NotInitialized() {
        FakeTestContainer container = new FakeTestContainer();
        assertThatThrownBy(container::getClient).isInstanceOf(IllegalStateException.class);
    }

    /** Fake test container. */
    private static final class FakeTestContainer extends BaseTestContainer<FakeClient> {

        @Override
        protected FakeClient createClient() {
            return new FakeClient();
        }

        @Override
        protected void clean(FakeClient client) {
            client.clean();
        }

        @Override
        protected void closeClient(FakeClient client) {
            client.close();
        }
    }

    /** Fake client that simulates having a clean or dirty container. */
    private static final class FakeClient {

        private boolean isClean = false;
        private boolean isClosed = false;

        boolean isClean() {
            return isClean;
        }

        boolean isClosed() {
            return isClosed;
        }

        void doOps() {
            checkIsNotClosed();
            isClean = false;
        }

        void clean() {
            checkIsNotClosed();
            isClean = true;
        }

        void close() {
            isClosed = true;
        }

        private void checkIsNotClosed() {
            if (isClosed) {
                throw new IllegalStateException("closed");
            }
        }
    }
}
