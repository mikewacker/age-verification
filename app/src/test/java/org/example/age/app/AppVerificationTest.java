package org.example.age.app;

import static org.assertj.core.api.Assertions.assertThat;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import java.io.IOException;
import org.example.age.app.config.AvsAppConfig;
import org.example.age.app.config.SiteAppConfig;
import org.example.age.avs.api.client.AvsApi;
import org.example.age.common.api.AgeRange;
import org.example.age.common.api.VerificationRequest;
import org.example.age.common.api.VerifiedUser;
import org.example.age.common.api.crypto.SecureId;
import org.example.age.module.store.dynamodb.testing.DynamoDbTestContainer;
import org.example.age.module.store.redis.testing.RedisTestContainer;
import org.example.age.site.api.VerificationState;
import org.example.age.site.api.VerificationStatus;
import org.example.age.site.api.client.SiteApi;
import org.example.age.testing.client.TestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import retrofit2.Response;

public final class AppVerificationTest {

    @RegisterExtension
    private static final DropwizardAppExtension<SiteAppConfig> siteApp =
            new DropwizardAppExtension<>(SiteApp.class, ResourceHelpers.resourceFilePath("config-site.yaml"));

    @RegisterExtension
    private static final DropwizardAppExtension<AvsAppConfig> avsApp =
            new DropwizardAppExtension<>(AvsApp.class, ResourceHelpers.resourceFilePath("config-avs.yaml"));

    @RegisterExtension
    private static final RedisTestContainer redis = new RedisTestContainer();

    @RegisterExtension
    private static final DynamoDbTestContainer dynamoDb = new DynamoDbTestContainer();

    private static final SiteApi siteClient = createClient(8080, "username", SiteApi.class);
    private static final AvsApi avsClient = createClient(9090, "person", AvsApi.class);

    @BeforeAll
    public static void setUpContainers() {
        dynamoDb.createSiteAccountStoreTables();
        dynamoDb.createAvsAccountStoreTables();
        VerifiedUser user = VerifiedUser.builder()
                .pseudonym(SecureId.fromString("uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4"))
                .ageRange(AgeRange.builder().min(40).max(40).build())
                .build();
        dynamoDb.createAvsAccount("person", user);
    }

    @Test
    public void verify() throws IOException {
        Response<VerificationRequest> requestResponse =
                siteClient.createVerificationRequest().execute();
        assertThat(requestResponse.isSuccessful()).isTrue();
        SecureId requestId = requestResponse.body().getId();

        Response<Void> linkResponse =
                avsClient.linkVerificationRequest(requestId).execute();
        assertThat(linkResponse.isSuccessful()).isTrue();

        Response<Void> sendResponse = avsClient.sendAgeCertificate().execute();
        assertThat(sendResponse.isSuccessful()).isTrue();

        Response<VerificationState> stateResponse =
                siteClient.getVerificationState().execute();
        assertThat(stateResponse.isSuccessful()).isTrue();
        VerificationState state = stateResponse.body();
        assertThat(state.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(state.getUser().getPseudonym())
                .isEqualTo(SecureId.fromString("wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI"));
        assertThat(state.getUser().getAgeRange())
                .isEqualTo(AgeRange.builder().min(18).build());
    }

    private static <A> A createClient(int port, String accountId, Class<A> apiType) {
        return TestClient.api(port, requestBuilder -> requestBuilder.header("Account-Id", accountId), apiType);
    }
}
