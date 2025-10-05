plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    api(platform(libs.dropwizard.bom))
    api(libs.dropwizard.core)

    implementation(project(":avs:api"))
    implementation(project(":service"))
    implementation(project(":common:provider:account-demo"))
    implementation(project(":avs:client:site"))
    implementation(project(":module:store-dynamodb"))
    implementation(project(":common:provider:pendingstore-redis"))
    implementation(project(":module:crypto-demo"))
    implementation(project(":common:app"))
    implementation(libs.jacksonCore.annotations)
    implementation(libs.jakartaValidation.api)

    // Dagger component
    implementation(platform(libs.aws.bom))
    implementation(project(":site:api"))
    implementation(project(":avs:spi"))
    implementation(project(":common:client:api"))
    implementation(project(":common:env"))
    implementation(libs.aws.dynamoDb)
    implementation(libs.darc.darc)
    implementation(libs.jedis.jedis)
}
