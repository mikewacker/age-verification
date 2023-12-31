package org.example.age.data.certificate;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import org.junit.jupiter.api.Test;

public final class VerificationSessionTest {

    @Test
    public void serializeThenDeserialize() {
        VerificationSession session = VerificationSession.generate(VerificationRequestTest.createVerificationRequest());
        JsonTester.serializeThenDeserialize(session, new TypeReference<>() {});
    }
}
