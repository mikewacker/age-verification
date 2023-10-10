package org.example.age.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.example.age.data.AgeRange;
import org.example.age.data.AgeThresholds;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.data.certificate.AgeCertificate;
import org.example.age.data.certificate.VerificationRequest;
import org.example.age.testing.TestKeyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class AvsVerificationComponentTest {

    private static final String REAL_NAME = "First Last";
    private static final String USERNAME = "username";

    private AvsUi avsUi;
    private AvsApi avsApi;
    private VerifiedUserStore avsUserStore;
    private SiteUi mockSiteUi;
    private Supplier<AgeCertificate> mockSiteCertificateStore;

    private AgeThresholds ageThresholds;
    private SecureId remotePseudonymKey;

    @BeforeEach
    public void setUpComponents() {
        // Set up the age verification service.
        AvsVerificationComponent avs = AvsVerificationComponent.create("MyAVS", TestKeyStore.avsSigningKeyPair());
        avsUi = avs;
        avsApi = avs;
        avsUserStore = avs;
        assertThat(avsUserStore.getName()).isEqualTo("MyAVS");

        // Register people with the age verification service.
        VerifiedUser avsUser = VerifiedUser.of(SecureId.generate(), 40);
        avs.registerPerson(REAL_NAME, avsUser);

        // Set up the site.
        String siteId = "MySite";
        MockSite mockSite = MockSite.create(siteId, avs);
        mockSiteUi = mockSite;
        mockSiteCertificateStore = mockSite;

        // Register the site with the age verification service.
        ageThresholds = AgeThresholds.of(18);
        remotePseudonymKey = TestKeyStore.remotePseudonymKey();
        avs.registerSite(siteId, mockSite, ageThresholds, remotePseudonymKey);
    }

    @Test
    public void verifyAccount() {
        VerificationRequest request = mockSiteUi.createVerificationRequest(USERNAME);
        avsUi.processVerificationRequest(REAL_NAME, request.id());
        AgeCertificate certificate = mockSiteCertificateStore.get();
        assertThat(certificate.verificationRequest()).isEqualTo(request);
    }

    @Test
    public void anonymizeUser() {
        VerificationRequest requestId = mockSiteUi.createVerificationRequest(USERNAME);
        avsUi.processVerificationRequest(REAL_NAME, requestId.id());
        AgeCertificate certificate = mockSiteCertificateStore.get();
        VerifiedUser avsUser = avsUserStore.retrieveVerifiedUser(REAL_NAME);
        VerifiedUser expectedCertificateUser =
                avsUser.anonymizeAge(ageThresholds).localize(remotePseudonymKey);
        assertThat(certificate.verifiedUser()).isEqualTo(expectedCertificateUser);
    }

    @Test
    public void error_ProcessVerificationRequest_VerificationRequestNotFound() {
        SecureId fakeRequestId = SecureId.generate();
        error_processVerificationRequest(REAL_NAME, fakeRequestId, "verification request not found");
    }

    @Test
    public void error_ProcessVerificationRequest_PersonNotFound() {
        VerificationRequest request = mockSiteUi.createVerificationRequest(USERNAME);
        error_processVerificationRequest("DNE", request.id(), "person not found");
    }

    private void error_processVerificationRequest(String realName, SecureId requestId, String expectedMessage) {
        assertThatThrownBy(() -> avsUi.processVerificationRequest(realName, requestId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void error_GenerateVerificationRequestForSite_SiteNotRegistered() {
        assertThatThrownBy(() -> avsApi.generateVerificationRequestForSite("DNE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("site not registered");
    }

    @Test
    public void error_RetrieveVerifiedUser_PersonNotFound() {
        assertThatThrownBy(() -> avsUserStore.retrieveVerifiedUser("DNE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("person not found");
    }

    /** Mock site that stores certificates it receives. */
    private static final class MockSite implements SiteUi, SiteApi, Supplier<AgeCertificate> {

        private final String siteId;
        private final AvsApi avsApi;

        private Optional<AgeCertificate> maybeCertificate = Optional.empty();

        public static MockSite create(String siteId, AvsApi avsApi) {
            return new MockSite(siteId, avsApi);
        }

        @Override
        public AgeCertificate get() {
            assertThat(maybeCertificate).isPresent();
            return maybeCertificate.get();
        }

        @Override
        public VerificationRequest createVerificationRequest(String username) {
            return avsApi.generateVerificationRequestForSite(siteId);
        }

        @Override
        public void processAgeCertificate(byte[] signedCertificate) {
            AgeCertificate certificate =
                    AgeCertificate.verifyForSite(signedCertificate, avsApi.getPublicSigningKey(), siteId);
            maybeCertificate = Optional.of(certificate);
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isVerified(String username) {
            throw new UnsupportedOperationException();
        }

        @Override
        public AgeRange getAgeRange(String username) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<String> getGuardians(String username) {
            throw new UnsupportedOperationException();
        }

        private MockSite(String siteId, AvsApi avsApi) {
            this.siteId = siteId;
            this.avsApi = avsApi;
        }
    }
}
