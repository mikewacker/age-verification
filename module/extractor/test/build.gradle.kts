plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(project(":core:api:extractors"))
    testFixturesApi(libs.dagger.dagger)
    testFixturesApi(libs.jakartaInject.api)

    testFixturesImplementation(libs.drift.api)
    testFixturesImplementation(libs.undertow.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":core:api:extractors"))
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jakartaInject.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.undertow.core)
}
