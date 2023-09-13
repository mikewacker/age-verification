package org.example.age.demo;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.age.certificate.AgeCertificate;
import org.example.age.certificate.VerificationRequest;
import org.example.age.data.AgeRange;
import org.example.age.data.VerifiedUser;
import org.example.age.verification.AvsUi;
import org.example.age.verification.SiteUi;
import org.example.age.verification.VerifiedUserStore;
import org.junit.jupiter.api.Test;

public class DemoTest {

    @Test
    public void verifyAccounts() {
        Demo demo = Demo.create();
        verifyAccountsForSite(demo, Demo.SITE1_NAME, Demo.PARENT_SITE1_USERNAME, Demo.CHILD_SITE1_USERNAME);
        verifyAccountsForSite(demo, Demo.SITE2_NAME, Demo.PARENT_SITE2_USERNAME, Demo.CHILD_SITE2_USERNAME);
    }

    private void verifyAccountsForSite(Demo demo, String siteId, String parentUsername, String childUsername) {
        AvsUi avsUi = demo.avsUi();
        SiteUi siteUi = demo.siteUi(siteId);

        // Verify the parent.
        assertThat(siteUi.isVerified(parentUsername)).isFalse();
        VerificationRequest parentRequest = siteUi.createVerificationRequest(parentUsername);
        avsUi.processVerificationRequest(Demo.PARENT_REAL_NAME, parentRequest.id());
        assertThat(siteUi.isVerified(parentUsername)).isTrue();
        assertThat(siteUi.getAgeRange(parentUsername)).isEqualTo(AgeRange.atOrAbove(18));
        assertThat(siteUi.getGuardians(parentUsername)).isEmpty();

        // Verify the child.
        assertThat(siteUi.isVerified(childUsername)).isFalse();
        VerificationRequest childRequest = siteUi.createVerificationRequest(childUsername);
        avsUi.processVerificationRequest(Demo.CHILD_REAL_NAME, childRequest.id());
        assertThat(siteUi.isVerified(childUsername)).isTrue();
        assertThat(siteUi.getAgeRange(childUsername)).isEqualTo(AgeRange.of(13, 18));
        assertThat(siteUi.getGuardians(childUsername)).containsExactly(parentUsername);
    }

    @Test
    public void retrieveVerifiedUsers() {
        Demo demo = Demo.create();

        // Retrieve users for the age verification service.
        retrieveVerifiedUsersForSite(demo, Demo.AVS_NAME, Demo.PARENT_REAL_NAME, Demo.CHILD_REAL_NAME);

        // Retrieve users for the sites.
        verifyAccountsForSite(demo, Demo.SITE1_NAME, Demo.PARENT_SITE1_USERNAME, Demo.CHILD_SITE1_USERNAME);
        retrieveVerifiedUsersForSite(demo, Demo.SITE1_NAME, Demo.PARENT_SITE1_USERNAME, Demo.CHILD_SITE1_USERNAME);

        verifyAccountsForSite(demo, Demo.SITE2_NAME, Demo.PARENT_SITE2_USERNAME, Demo.CHILD_SITE2_USERNAME);
        retrieveVerifiedUsersForSite(demo, Demo.SITE2_NAME, Demo.PARENT_SITE2_USERNAME, Demo.CHILD_SITE2_USERNAME);
    }

    private void retrieveVerifiedUsersForSite(Demo demo, String name, String parentUsername, String childUsername) {
        VerifiedUserStore userStore = demo.verifiedUserStore(name);

        // Retrieve the parent.
        VerifiedUser parent = userStore.retrieveVerifiedUser(parentUsername);
        assertThat(parent.guardianPseudonyms()).isEmpty();

        // Retrieve the child.
        VerifiedUser child = userStore.retrieveVerifiedUser(childUsername);
        assertThat(child.guardianPseudonyms()).containsExactly(parent.pseudonym());
    }

    @Test
    public void getAgeCertificates() {
        Demo demo = Demo.create();
        getAgeCertificatesForSite(demo, Demo.SITE1_NAME, Demo.PARENT_SITE1_USERNAME, Demo.CHILD_SITE1_USERNAME);
        getAgeCertificatesForSite(demo, Demo.SITE2_NAME, Demo.PARENT_SITE2_USERNAME, Demo.CHILD_SITE2_USERNAME);
    }

    private void getAgeCertificatesForSite(Demo demo, String siteId, String parentUsername, String childUsername) {
        AvsUi avsUi = demo.avsUi();
        SiteUi siteUi = demo.siteUi(siteId);

        // Get the certificate for the parent.
        VerificationRequest parentRequest = siteUi.createVerificationRequest(parentUsername);
        avsUi.processVerificationRequest(Demo.PARENT_REAL_NAME, parentRequest.id());
        AgeCertificate parentCertificate = demo.ageCertificate(parentRequest.id());
        assertThat(parentCertificate.verificationRequest()).isEqualTo(parentRequest);
        assertThat(parentCertificate.verifiedUser().ageRange()).isEqualTo(AgeRange.atOrAbove(18));
        assertThat(parentCertificate.verifiedUser().guardianPseudonyms()).isEmpty();

        // Get the certificate for the child.
        VerificationRequest childRequest = siteUi.createVerificationRequest(childUsername);
        avsUi.processVerificationRequest(Demo.CHILD_REAL_NAME, childRequest.id());
        AgeCertificate childCertificate = demo.ageCertificate(childRequest.id());
        assertThat(childCertificate.verificationRequest()).isEqualTo(childRequest);
        assertThat(childCertificate.verifiedUser().ageRange()).isEqualTo(AgeRange.of(13, 18));
        assertThat(childCertificate.verifiedUser().guardianPseudonyms())
                .containsExactly(parentCertificate.verifiedUser().pseudonym());
    }
}
