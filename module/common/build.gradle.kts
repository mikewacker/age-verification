plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(libs.junitJupiter.api)
    testFixturesImplementation(project(":testing"))
    testFixturesImplementation(libs.bundles.dagger)
    testFixturesImplementation(libs.bundles.json)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
    testImplementation(libs.bundles.dropwizard)
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
