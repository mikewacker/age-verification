package org.example.age.avs.spi;

import java.io.IOException;
import org.example.age.testing.api.TestModels;
import org.example.age.testing.json.JsonTesting;
import org.junit.jupiter.api.Test;

public final class VerifiedAccountTest {

    @Test
    public void serializeThenDeserialize() throws IOException {
        VerifiedAccount account = VerifiedAccount.builder()
                .id("person")
                .user(TestModels.createVerifiedUser())
                .build();
        JsonTesting.serializeThenDeserialize(account, VerifiedAccount.class);
    }
}
