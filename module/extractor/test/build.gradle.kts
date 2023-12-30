plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(project(":core:api:extractors"))
    testFixturesApi(libs.dagger.dagger)
    testFixturesApi(libs.javaxInject.inject)

    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(libs.undertow.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:extractors"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.javaxInject.inject)
    testImplementation(libs.mockito.core)
    testImplementation(libs.undertow.core)
}
