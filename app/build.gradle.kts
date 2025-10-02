plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    // configuration
    api(project(":service"))
    api(project(":module:client"))
    api(project(":module:store-redis"))
    api(project(":module:store-dynamodb"))
    api(project(":module:crypto-demo"))

    api(libs.bundles.dropwizard)
    implementation(project(":site:api"))
    implementation(project(":avs:api"))
    implementation(project(":common:provider:request-demo"))
    implementation(project(":common:app"))
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.json)

    // Dagger component
    implementation(project(":common:spi"))
    implementation(project(":common:env"))
    implementation(project(":service:module"))
    implementation(libs.bundles.darc)
    implementation(libs.bundles.dynamoDb)
    implementation(libs.bundles.redis)

    testImplementation(project(":testing"))
    testImplementation(testFixtures(project(":module:store-redis")))
    testImplementation(testFixtures(project(":module:store-dynamodb")))
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
