plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(libs.bundles.dagger)
    api(libs.bundles.jaxRs)
    api(libs.bundles.json)
    implementation(libs.dropwizard.jersey)

    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(libs.bundles.dagger)
    testFixturesApi(libs.junitJupiter.api)
    testFixturesImplementation(testFixtures(project(":common")))
    testFixturesImplementation(libs.bundles.json)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(libs.bundles.dropwizard)
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
