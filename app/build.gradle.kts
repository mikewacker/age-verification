plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    api(platform(libs.dropwizard.bom))
    api(libs.dropwizard.core)

    implementation(project(":site:api"))
    implementation(project(":avs:api"))
    implementation(project(":service"))
    implementation(project(":common:provider:request-demo"))
    implementation(project(":module:client"))
    implementation(project(":module:store-redis"))
    implementation(project(":module:store-dynamodb"))
    implementation(project(":module:crypto-demo"))
    implementation(project(":common:app"))
    implementation(libs.jacksonCore.annotations)
    implementation(libs.jakartaValidation.api)

    // Dagger component
    implementation(platform(libs.aws.bom))
    implementation(project(":site:spi"))
    implementation(project(":avs:spi"))
    implementation(project(":common:env"))
    implementation(libs.aws.dynamoDb)
    implementation(libs.darc.darc)
    implementation(libs.jedis.jedis)

    testImplementation(testFixtures(project(":module:store-redis")))
    testImplementation(testFixtures(project(":module:store-dynamodb")))
    testImplementation(libs.dropwizard.testing)
}

dockerCompose {
    isRequiredBy(tasks.test)
}
