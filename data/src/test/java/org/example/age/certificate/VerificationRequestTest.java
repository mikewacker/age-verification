package org.example.age.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.assertj.core.data.TemporalUnitWithinOffset;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public final class VerificationRequestTest {

    private static final String SITE_ID = "MySite";
    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);

    private static ObjectMapper mapper;

    @BeforeAll
    public static void createMapper() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void generateForSite() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN);
        assertThat(request.id()).isNotNull();
        assertThat(request.siteId()).isEqualTo(SITE_ID);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        assertThat(request.expiration())
                .isCloseTo(now.plus(EXPIRES_IN), new TemporalUnitWithinOffset(1, ChronoUnit.SECONDS));
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
    public void serializeThenDeserialize() throws JsonProcessingException {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN);
        String json = mapper.writeValueAsString(request);
        VerificationRequest deserializedRequest = mapper.readValue(json, VerificationRequest.class);
        assertThat(deserializedRequest).isEqualTo(request);
    }
}
