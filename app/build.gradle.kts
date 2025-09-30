plugins {
    `java-library`
    `java-test-fixtures`
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
    implementation(project(":api"))
    implementation(project(":module:request-demo"))
    implementation(project(":module:common"))
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.json)

    // Dagger component
    implementation(project(":service:module"))
    implementation(libs.bundles.darc)
    implementation(libs.bundles.dynamoDb)
    implementation(libs.bundles.redis)

    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(libs.bundles.dropwizard)
    testFixturesImplementation(project(":service"))
    testFixturesImplementation(testFixtures(project(":module:test")))
    testFixturesImplementation(libs.bundles.dagger)
    testFixturesImplementation(libs.bundles.retrofit)

    // Dagger component
    testFixturesImplementation(project(":service:module"))

    testImplementation(project(":testing"))
    testImplementation(testFixtures(project(":module:store-redis")))
    testImplementation(testFixtures(project(":module:store-dynamodb")))
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
