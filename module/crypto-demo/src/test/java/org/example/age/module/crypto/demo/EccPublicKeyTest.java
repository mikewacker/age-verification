package org.example.age.module.crypto.demo;

import java.io.IOException;
import org.example.age.module.crypto.demo.testing.ConfigKeyPair;
import org.example.age.testing.JsonTesting;
import org.junit.jupiter.api.Test;

public final class EccPublicKeyTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        JsonTesting.serializeThenDeserialize(ConfigKeyPair.publicKey(), EccPublicKey.class);
    }
}
