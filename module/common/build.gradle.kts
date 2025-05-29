plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(libs.dagger.dagger)
    api(libs.jackson.databind)
    api(libs.jakartaInject.api)
    api(libs.jaxRs.api)

    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(libs.dagger.dagger)
    testFixturesApi(libs.jackson.databind)
    testFixturesApi(libs.jakartaInject.api)
    testFixturesImplementation(testFixtures(project(":common")))

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(libs.okhttp.okhttp)
    testImplementation(libs.dropwizard.core)
    testImplementation(libs.dropwizard.testing)
}
