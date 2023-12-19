package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import org.assertj.core.data.Offset;
import org.example.age.data.json.JsonValues;
import org.junit.jupiter.api.Test;

public final class VerificationRequestTest {

    private static final String SITE_ID = "Site";
    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);
    private static final String REDIRECT_PATH = "/verify?request-id=%s";

    @Test
    public void generateForSite() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN, REDIRECT_PATH);
        assertThat(request.id()).isNotNull();
        assertThat(request.siteId()).isEqualTo(SITE_ID);
        long now = System.currentTimeMillis() / 1000;
        long expectedExpiration = now + EXPIRES_IN.toSeconds();
        assertThat(request.expiration()).isCloseTo(expectedExpiration, Offset.offset(1L));
        String expectedRedirectPath = String.format("/verify?request-id=%s", request.id());
        assertThat(request.redirectUrl()).isEqualTo(expectedRedirectPath);
    }

    @Test
    public void convertRedirectPathToUrl() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN, REDIRECT_PATH);
        VerificationRequest convertedRequest = request.convertRedirectPathToUrl("http://localhost:80/");
        String expectedRedirectPath = String.format("http://localhost/verify?request-id=%s", request.id());
        assertThat(convertedRequest.redirectUrl()).isEqualTo(expectedRedirectPath);
    }

    @Test
    public void isIntendedRecipient_IntendedRecipient() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN, REDIRECT_PATH);
        assertThat(request.isIntendedRecipient(SITE_ID)).isTrue();
    }

    @Test
    public void isIntendedRecipient_WrongRecipient() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN, REDIRECT_PATH);
        assertThat(request.isIntendedRecipient("OtherSite")).isFalse();
    }

    @Test
    public void isExpired_NotExpired() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN, REDIRECT_PATH);
        assertThat(request.isExpired()).isFalse();
    }

    @Test
    public void isExpired_Expired() {
        VerificationRequest request =
                VerificationRequest.generateForSite(SITE_ID, Duration.ofMinutes(-1), REDIRECT_PATH);
        assertThat(request.isExpired()).isTrue();
    }

    @Test
    public void serializeThenDeserialize() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN, REDIRECT_PATH);
        byte[] rawRequest = JsonValues.serialize(request);
        VerificationRequest rtRequest = JsonValues.deserialize(rawRequest, new TypeReference<>() {});
        assertThat(rtRequest).isEqualTo(request);
    }
}
