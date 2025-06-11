package org.example.age.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.example.age.common.testing.WebStageTesting.await;
import static org.example.age.common.testing.WebStageTesting.awaitErrorCode;

import jakarta.ws.rs.NotFoundException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import org.example.age.api.AgeCertificate;
import org.example.age.api.AgeRange;
import org.example.age.api.AuthMatchData;
import org.example.age.api.SignedAgeCertificate;
import org.example.age.api.VerificationRequest;
import org.example.age.api.VerificationState;
import org.example.age.api.client.SiteApi;
import org.example.age.api.crypto.SecureId;
import org.example.age.api.testing.TestSignatures;
import org.example.age.service.testing.TestAvsService;
import org.example.age.service.testing.TestAvsServiceComponent;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

public final class AvsServiceTest {

    private static final AuthMatchData EMPTY_DATA =
            AuthMatchData.builder().name("").data("").build();

    private final TestAvsService avsService = TestAvsServiceComponent.create(this::getSiteClient);

    private final Map<String, SiteApi> siteClients =
            Map.of("site1", new FakeSiteClient(), "site2", new FakeSiteClient());
    private AgeCertificate ageCertificate = null;

    @Test
    public void verify() {
        avsService.setAccountId("person");
        VerificationRequest request1 = await(avsService.createVerificationRequestForSite("site1", EMPTY_DATA));
        assertThat(request1.getSiteId()).isEqualTo("site1");
        OffsetDateTime expectedExpiration = OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMinutes(5));
        assertThat(request1.getExpiration()).isCloseTo(expectedExpiration, within(1, ChronoUnit.SECONDS));

        await(avsService.linkVerificationRequest(request1.getId()));
        await(avsService.sendAgeCertificate());
        assertThat(ageCertificate.getRequest()).isEqualTo(request1);
        SecureId pseudonym1 = ageCertificate.getUser().getPseudonym();
        assertThat(ageCertificate.getUser().getAgeRange())
                .isEqualTo(AgeRange.builder().min(18).build());

        ageCertificate = null;
        VerificationRequest request2 = await(avsService.createVerificationRequestForSite("site2", EMPTY_DATA));
        await(avsService.linkVerificationRequest(request2.getId()));
        await(avsService.sendAgeCertificate());
        SecureId pseudonym2 = ageCertificate.getUser().getPseudonym();
        assertThat(pseudonym2).isNotEqualTo(pseudonym1);
    }

    @Test
    public void error_Unauthenticated() {
        awaitErrorCode(avsService.linkVerificationRequest(SecureId.generate()), 401);
        awaitErrorCode(avsService.sendAgeCertificate(), 401);
    }

    @Test
    public void error_UnverifiedPerson() {
        avsService.setAccountId("unverified-person");
        awaitErrorCode(avsService.linkVerificationRequest(SecureId.generate()), 403);
        awaitErrorCode(avsService.sendAgeCertificate(), 403);
    }

    @Test
    public void error_LinkVerificationRequestTwice() {
        avsService.setAccountId("person");
        VerificationRequest request = await(avsService.createVerificationRequestForSite("site1", EMPTY_DATA));
        await(avsService.linkVerificationRequest(request.getId()));
        awaitErrorCode(avsService.linkVerificationRequest(request.getId()), 404);
    }

    @Test
    public void error_SendAgeCertificateTwice() {
        avsService.setAccountId("person");
        VerificationRequest request = await(avsService.createVerificationRequestForSite("site1", EMPTY_DATA));
        await(avsService.linkVerificationRequest(request.getId()));
        await(avsService.sendAgeCertificate());
        awaitErrorCode(avsService.sendAgeCertificate(), 404);
    }

    @Test
    public void error_VerificationRequestNotFound() {
        avsService.setAccountId("person");
        awaitErrorCode(avsService.linkVerificationRequest(SecureId.generate()), 404);
        awaitErrorCode(avsService.sendAgeCertificate(), 404);
    }

    @Test
    public void error_UnregisteredSite() {
        avsService.setAccountId("person");
        awaitErrorCode(avsService.createVerificationRequestForSite("unregistered-site", EMPTY_DATA), 404);
    }

    private SiteApi getSiteClient(String siteId) {
        return Optional.ofNullable(siteClients.get(siteId)).orElseThrow(NotFoundException::new);
    }

    /** Fake client implementation of {@link SiteApi}. */
    private final class FakeSiteClient implements SiteApi {

        @Override
        public Call<VerificationState> getVerificationState() {
            return Calls.failure(new UnsupportedOperationException());
        }

        @Override
        public Call<VerificationRequest> createVerificationRequest() {
            return Calls.failure(new UnsupportedOperationException());
        }

        @Override
        public Call<Void> processAgeCertificate(SignedAgeCertificate signedAgeCertificate) {
            ageCertificate = TestSignatures.verify(signedAgeCertificate);
            return Calls.response(Response.success(null));
        }
    }
}
