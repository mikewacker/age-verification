package org.example.age.certificate;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import org.assertj.core.data.TemporalUnitWithinOffset;
import org.example.age.internal.SerializationUtils;
import org.junit.jupiter.api.Test;

public final class VerificationRequestTest {

    private static final String SITE_ID = "MySite";
    private static final Duration EXPIRES_IN = Duration.ofMinutes(5);

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
    public void serializeThenDeserialize() {
        VerificationRequest request = VerificationRequest.generateForSite(SITE_ID, EXPIRES_IN);
        byte[] bytes = SerializationUtils.serialize(request);
        VerificationRequest deserializedRequest = SerializationUtils.deserialize(bytes, VerificationRequest.class);
        assertThat(deserializedRequest).isEqualTo(request);
    }
}
