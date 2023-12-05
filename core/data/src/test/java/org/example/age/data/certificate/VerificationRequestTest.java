package org.example.age.data.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

public final class VerificationRequestTest {

    private static final String SITE_ID = "Site";
    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);

    @Test
    public void generateForSite() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN);
        assertThat(request.id()).isNotNull();
        assertThat(request.siteId()).isEqualTo(SITE_ID);
        long now = System.currentTimeMillis() / 1000;
        long expectedExpiration = now + EXPIRES_IN.toSeconds();
        assertThat(request.expiration()).isCloseTo(expectedExpiration, Offset.offset(1L));
    }

    @Test
    public void isIntendedRecipient_IntendedRecipient() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN);
        assertThat(request.isIntendedRecipient(SITE_ID)).isTrue();
    }

    @Test
    public void isIntendedRecipient_WrongRecipient() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN);
        assertThat(request.isIntendedRecipient("OtherSite")).isFalse();
    }

    @Test
    public void isExpired_NotExpired() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN);
        assertThat(request.isExpired()).isFalse();
    }

    @Test
    public void isExpired_Expired() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, Duration.ofMinutes(-1));
        assertThat(request.isExpired()).isTrue();
    }

    @Test
    public void serializeThenDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN);
        byte[] rawRequest = mapper.writeValueAsBytes(request);
        VerificationRequest rtRequest = mapper.readValue(rawRequest, new TypeReference<>() {});
        assertThat(rtRequest).isEqualTo(request);
    }
}
