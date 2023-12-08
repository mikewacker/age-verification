package org.example.age.testing.server;

/** Factory that creates a server on localhost that listens on the specified port. */
@FunctionalInterface
public interface TestServerFactory<T> {

    T create(int port);
}
