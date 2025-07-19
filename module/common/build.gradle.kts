plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(libs.bundles.json)
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.dropwizard)

    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(libs.junitJupiter.api)
    testFixturesImplementation(testFixtures(project(":common")))
    testFixturesImplementation(libs.bundles.dagger)
    testFixturesImplementation(libs.bundles.json)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(libs.bundles.dropwizard)
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
