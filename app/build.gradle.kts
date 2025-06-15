plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(project(":service"))
    api(project(":module:client"))
    api(project(":module:store-redis"))
    api(project(":module:store-dynamodb"))
    api(project(":module:crypto-demo"))
    api(libs.bundles.dropwizard)
    api(libs.bundles.json)
    implementation(project(":module:request-demo"))
    implementation(libs.bundles.dynamoDb) // for Dagger
    implementation(libs.bundles.redis) // for Dagger

    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(libs.bundles.dropwizard)
    testFixturesImplementation(project(":service"))
    testFixturesImplementation(testFixtures(project(":module:test")))
    testFixturesImplementation(libs.bundles.retrofit)

    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":module:store-redis")))
    testImplementation(testFixtures(project(":module:store-dynamodb")))
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
