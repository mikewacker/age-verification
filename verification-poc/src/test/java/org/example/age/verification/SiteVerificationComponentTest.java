package org.example.age.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert;
import org.example.age.certificate.AgeCertificate;
import org.example.age.certificate.VerificationRequest;
import org.example.age.data.AgeRange;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.testing.TestKeyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class SiteVerificationComponentTest {

    private static final String PARENT_REAL_NAME = "John Smith";
    private static final String PARENT_USERNAME = "JohnS";
    private static final String CHILD_REAL_NAME = "Bobby Smith";
    private static final String CHILD_USERNAME = "BobbyS";

    private SiteUi siteUi;
    private VerifiedUserStore siteUserStore;
    private AvsUi mockAvsUi;
    private AvsApi mockAvsApi;
    private VerifiedUserStore mockAvsCertificateUserStore;

    private SecureId localSiteIdKey;

    @BeforeEach
    public void setUpComponents() {
        // Set up the age verification service.
        MockAvs mockAvs = MockAvs.create(TestKeyStore.avsSigningKeyPair());
        mockAvsUi = mockAvs;
        mockAvsApi = mockAvs;
        mockAvsCertificateUserStore = mockAvs;

        // Register people with the age verification service.
        VerifiedUser certificateParent = VerifiedUser.of(SecureId.generate(), AgeRange.atOrAbove(18), List.of());
        mockAvs.registerPerson(PARENT_REAL_NAME, certificateParent);

        VerifiedUser certificateChild =
                VerifiedUser.of(SecureId.generate(), AgeRange.of(13, 18), List.of(certificateParent.id()));
        mockAvs.registerPerson(CHILD_REAL_NAME, certificateChild);

        // Set up the site.
        localSiteIdKey = TestKeyStore.localSiteIdKey();
        SiteVerificationComponent site = SiteVerificationComponent.create("MySite", localSiteIdKey, mockAvs);
        siteUi = site;
        siteUserStore = site;
        assertThat(siteUserStore.getName()).isEqualTo("MySite");

        // Register the site with the age verification service.
        mockAvs.registerSite(site);
    }

    @Test
    public void verifyParentAndChild() {
        // Verify the parent.
        assertThat(siteUi.isVerified(PARENT_USERNAME)).isFalse();
        VerificationRequest parentRequest = siteUi.createVerificationRequest(PARENT_USERNAME);
        mockAvsUi.processVerificationRequest(PARENT_REAL_NAME, parentRequest.id());
        assertThat(siteUi.isVerified(PARENT_USERNAME)).isTrue();
        assertThat(siteUi.getAgeRange(PARENT_USERNAME)).isEqualTo(AgeRange.atOrAbove(18));
        assertThat(siteUi.getGuardians(PARENT_USERNAME)).isEmpty();

        // Verify the child.
        assertThat(siteUi.isVerified(CHILD_USERNAME)).isFalse();
        VerificationRequest childRequest = siteUi.createVerificationRequest(CHILD_USERNAME);
        mockAvsUi.processVerificationRequest(CHILD_REAL_NAME, childRequest.id());
        assertThat(siteUi.isVerified(CHILD_USERNAME)).isTrue();
        assertThat(siteUi.getAgeRange(CHILD_USERNAME)).isEqualTo(AgeRange.of(13, 18));
        assertThat(siteUi.getGuardians(CHILD_USERNAME)).containsExactly(PARENT_USERNAME);
    }

    @Test
    public void verifyChildOnly() {
        assertThat(siteUi.isVerified(CHILD_USERNAME)).isFalse();
        VerificationRequest childRequest = siteUi.createVerificationRequest(CHILD_USERNAME);
        mockAvsUi.processVerificationRequest(CHILD_REAL_NAME, childRequest.id());
        assertThat(siteUi.isVerified(CHILD_USERNAME)).isTrue();
        assertThat(siteUi.getAgeRange(CHILD_USERNAME)).isEqualTo(AgeRange.of(13, 18));
        assertThat(siteUi.getGuardians(CHILD_USERNAME)).isEmpty();
    }

    @Test
    public void reVerifyAccount() {
        VerificationRequest request1 = siteUi.createVerificationRequest(PARENT_USERNAME);
        mockAvsUi.processVerificationRequest(PARENT_REAL_NAME, request1.id());
        VerificationRequest request2 = siteUi.createVerificationRequest(PARENT_USERNAME);
        mockAvsUi.processVerificationRequest(PARENT_REAL_NAME, request2.id());
        assertThat(siteUi.isVerified(PARENT_USERNAME)).isTrue();
    }

    @Test
    public void anonymizeUser() {
        VerificationRequest request = siteUi.createVerificationRequest(CHILD_USERNAME);
        mockAvsUi.processVerificationRequest(CHILD_REAL_NAME, request.id());
        VerifiedUser certificateUser = mockAvsCertificateUserStore.retrieveVerifiedUser(CHILD_REAL_NAME);
        VerifiedUser siteUser = siteUserStore.retrieveVerifiedUser(CHILD_USERNAME);
        VerifiedUser expectedSiteUser = certificateUser.localize(localSiteIdKey);
        assertThat(siteUser).isEqualTo(expectedSiteUser);
    }

    @Test
    public void error_ProcessAgeCertificate_OnePersonVerifiesMultipleAccounts() {
        VerificationRequest request1 = siteUi.createVerificationRequest(PARENT_USERNAME);
        mockAvsUi.processVerificationRequest(PARENT_REAL_NAME, request1.id());
        VerificationRequest request2 = siteUi.createVerificationRequest(CHILD_USERNAME);
        assertThatThrownBy(() -> mockAvsUi.processVerificationRequest(PARENT_REAL_NAME, request2.id()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("one person cannot verify multiple accounts on a single site");
    }

    @Test
    public void error_ProcessAgeCertificate_VerificationRequestNotLinkedToUsername() {
        VerificationRequest fakeRequest = mockAvsApi.generateVerificationRequestForSite("MySite");
        assertThatThrownBy(() -> mockAvsUi.processVerificationRequest(CHILD_REAL_NAME, fakeRequest.id()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("verification request is not linked to a username");
    }

    @Test
    public void error_GetVerifiedUserData_UserNotVerified() {
        error_UserNotVerified(() -> siteUi.getAgeRange(CHILD_USERNAME));
        error_UserNotVerified(() -> siteUi.getGuardians(CHILD_USERNAME));
    }

    @Test
    public void error_RetrieveVerifiedUser_UserNotVerified() {
        error_UserNotVerified(() -> siteUserStore.retrieveVerifiedUser(CHILD_USERNAME));
    }

    private void error_UserNotVerified(ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user not verified");
    }

    /** Mock age verification service that stores the verified users that go on a certificate. */
    private static final class MockAvs implements AvsUi, AvsApi, VerifiedUserStore {

        private static final Duration EXPIRES_IN = Duration.ofMinutes(5);

        private final KeyPair signingKeyPair;

        private final Map<String, VerifiedUser> certificateUsers = new HashMap<>();
        private SiteApi siteApi;
        private final Map<SecureId, VerificationRequest> pendingRequests = new HashMap<>();

        public static MockAvs create(KeyPair signingKeyPair) {
            return new MockAvs(signingKeyPair);
        }

        @Override
        public PublicKey getPublicSigningKey() {
            return signingKeyPair.getPublic();
        }

        @Override
        public VerifiedUser retrieveVerifiedUser(String realName) {
            VerifiedUser user = certificateUsers.get(realName);
            assertThat(user).isNotNull();
            return user;
        }

        public void registerPerson(String realName, VerifiedUser certificateUser) {
            certificateUsers.put(realName, certificateUser);
        }

        public void registerSite(SiteApi siteApi) {
            this.siteApi = siteApi;
        }

        @Override
        public VerificationRequest generateVerificationRequestForSite(String siteId) {
            VerificationRequest request = VerificationRequest.generateForSite(siteId, EXPIRES_IN);
            pendingRequests.put(request.id(), request);
            return request;
        }

        @Override
        public void processVerificationRequest(String realName, SecureId requestId) {
            VerificationRequest request = retrievePendingVerificationRequest(requestId);
            VerifiedUser user = retrieveVerifiedUser(realName);
            AgeCertificate certificate = AgeCertificate.of(request, user);
            byte[] signedCertificate = certificate.sign(signingKeyPair.getPrivate());
            siteApi.processAgeCertificate(signedCertificate);
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException();
        }

        private VerificationRequest retrievePendingVerificationRequest(SecureId requestId) {
            VerificationRequest request = pendingRequests.remove(requestId);
            assertThat(request).isNotNull();
            return request;
        }

        private MockAvs(KeyPair signingKeyPair) {
            this.signingKeyPair = signingKeyPair;
        }
    }
}
