package org.example.age.verification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.example.age.certificate.VerificationRequest;
import org.example.age.data.AgeRange;
import org.example.age.data.AgeThresholds;
import org.example.age.data.SecureId;
import org.example.age.data.VerifiedUser;
import org.example.age.testing.TestKeyStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VerificationComponentsTest {

    private static final String PARENT_REAL_NAME = "John Smith";
    private static final String PARENT_USERNAME = "JohnS";
    private static final String CHILD_REAL_NAME = "Bobby Smith";
    private static final String CHILD_USERNAME = "BobbyS";

    private AvsUi avsUi;
    private SiteUi siteUi;

    private VerifiedUserStore avsUserStore;
    private VerifiedUserStore siteUserStore;
    private AgeThresholds ageThresholds;
    private SecureId remoteSiteIdKey;
    private SecureId localSiteIdKey;

    @BeforeEach
    public void setUpComponents() {
        // Set up the age verification service.
        AvsVerificationComponent avs = AvsVerificationComponent.create("MyAVS", TestKeyStore.avsSigningKeyPair());
        avsUi = avs;
        avsUserStore = avs;

        // Register people with the age verification service.
        VerifiedUser avsParent = VerifiedUser.of(SecureId.generate(), 40);
        avs.registerPerson(PARENT_REAL_NAME, avsParent);

        VerifiedUser avsChild = VerifiedUser.of(SecureId.generate(), 13, List.of(avsParent.id()));
        avs.registerPerson(CHILD_REAL_NAME, avsChild);

        // Set up the site.
        localSiteIdKey = TestKeyStore.localSiteIdKey();
        SiteVerificationComponent site = SiteVerificationComponent.create("MySite", localSiteIdKey, avs);
        siteUi = site;
        siteUserStore = site;

        // Register the site with the age verification service.
        ageThresholds = AgeThresholds.of(13, 18);
        remoteSiteIdKey = TestKeyStore.remoteSiteIdKey();
        avs.registerSite(site.getName(), site, ageThresholds, remoteSiteIdKey);
    }

    @Test
    public void verifyParentAndChild() {
        // Verify the parent.
        assertThat(siteUi.isVerified(PARENT_USERNAME)).isFalse();
        VerificationRequest parentRequest = siteUi.createVerificationRequest(PARENT_USERNAME);
        avsUi.processVerificationRequest(PARENT_REAL_NAME, parentRequest.id());
        assertThat(siteUi.isVerified(PARENT_USERNAME)).isTrue();
        assertThat(siteUi.getAgeRange(PARENT_USERNAME)).isEqualTo(AgeRange.atOrAbove(18));
        assertThat(siteUi.getGuardians(PARENT_USERNAME)).isEmpty();

        // Verify the child.
        assertThat(siteUi.isVerified(CHILD_USERNAME)).isFalse();
        VerificationRequest childRequest = siteUi.createVerificationRequest(CHILD_USERNAME);
        avsUi.processVerificationRequest(CHILD_REAL_NAME, childRequest.id());
        assertThat(siteUi.isVerified(CHILD_USERNAME)).isTrue();
        assertThat(siteUi.getAgeRange(CHILD_USERNAME)).isEqualTo(AgeRange.of(13, 18));
        assertThat(siteUi.getGuardians(CHILD_USERNAME)).containsExactly(PARENT_USERNAME);
    }

    @Test
    public void anonymizeUser() {
        VerificationRequest request = siteUi.createVerificationRequest(CHILD_USERNAME);
        avsUi.processVerificationRequest(CHILD_REAL_NAME, request.id());
        VerifiedUser avsUser = avsUserStore.retrieveVerifiedUser(CHILD_REAL_NAME);
        VerifiedUser siteUser = siteUserStore.retrieveVerifiedUser(CHILD_USERNAME);
        VerifiedUser expectedSiteUser =
                avsUser.anonymizeAge(ageThresholds).localize(remoteSiteIdKey).localize(localSiteIdKey);
        assertThat(siteUser).isEqualTo(expectedSiteUser);
    }

    @Test
    public void error_OnePersonVerifiesMultipleAccounts() {
        VerificationRequest request1 = siteUi.createVerificationRequest(PARENT_USERNAME);
        avsUi.processVerificationRequest(PARENT_REAL_NAME, request1.id());
        VerificationRequest request2 = siteUi.createVerificationRequest(CHILD_USERNAME);
        assertThatThrownBy(() -> avsUi.processVerificationRequest(PARENT_REAL_NAME, request2.id()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("one person cannot verify multiple accounts on a single site");
    }
}
