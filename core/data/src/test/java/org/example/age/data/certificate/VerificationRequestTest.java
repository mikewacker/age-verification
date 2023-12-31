package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.mikewacker.drift.testing.json.JsonTester;
import java.time.Duration;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

public final class VerificationRequestTest {

    @Test
    public void generateForSite() {
        VerificationRequest request = createVerificationRequest();
        assertThat(request.id()).isNotNull();
        assertThat(request.siteId()).isEqualTo("Site");
        long now = System.currentTimeMillis() / 1000;
        long expectedExpiration = now + Duration.ofMinutes(5).toSeconds();
        assertThat(request.expiration()).isCloseTo(expectedExpiration, Offset.offset(1L));
        String expectedRedirectPath = String.format("/verify?request-id=%s", request.id());
        assertThat(request.redirectUrl()).isEqualTo(expectedRedirectPath);
    }

    @Test
    public void convertRedirectPathToUrl() {
        VerificationRequest request = createVerificationRequest();
        VerificationRequest convertedRequest = request.convertRedirectPathToUrl("http://localhost/");
        String expectedRedirectPath = String.format("http://localhost/verify?request-id=%s", request.id());
        assertThat(convertedRequest.redirectUrl()).isEqualTo(expectedRedirectPath);
    }

    @Test
    public void isIntendedRecipient_IntendedRecipient() {
        VerificationRequest request = createVerificationRequest();
        assertThat(request.isIntendedRecipient("Site")).isTrue();
    }

    @Test
    public void isIntendedRecipient_WrongRecipient() {
        VerificationRequest request = createVerificationRequest();
        assertThat(request.isIntendedRecipient("Other Site")).isFalse();
    }

    @Test
    public void isExpired_NotExpired() {
        VerificationRequest request = createVerificationRequest();
        assertThat(request.isExpired()).isFalse();
    }

    @Test
    public void isExpired_Expired() {
        VerificationRequest request = VerificationRequest.generateForSite("Site", Duration.ofMinutes(-1), "");
        assertThat(request.isExpired()).isTrue();
    }

    @Test
    public void serializeThenDeserialize() {
        JsonTester.serializeThenDeserialize(createVerificationRequest(), new TypeReference<>() {});
    }

    public static VerificationRequest createVerificationRequest() {
        return VerificationRequest.generateForSite("Site", Duration.ofMinutes(5), "/verify?request-id=%s");
    }
}
