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

    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(libs.bundles.dagger)
    testFixturesApi(libs.bundles.json)
    testFixturesImplementation(testFixtures(project(":common")))

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(libs.bundles.dropwizard)
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
