plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)

    api(project(":core:api:types"))
    api(project(":core:api:extractors"))
    api(libs.dagger.dagger)
    api(libs.javaxInject.inject)
    api(libs.undertow.core)

    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":core:api:types"))
    implementation(project(":base:api:adapter"))
    implementation(project(":infra:api"))
    implementation(libs.drift.api)
    implementation(libs.jackson.core)

    // test fixtures
    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(libs.undertow.core)

    testFixturesImplementation(project(":crypto:data"))
    testFixturesImplementation(project(":core:data"))
    testFixturesImplementation(project(":core:api:types"))
    testFixturesImplementation(project(":core:api:extractors"))
    testFixturesImplementation(project(":module:extractor:builtin"))
    testFixturesImplementation(testFixtures(project(":module:extractor:test")))
    testFixturesImplementation(libs.dagger.dagger)
    testFixturesImplementation(libs.drift.api)
    testFixturesImplementation(libs.javaxInject.inject)

    // test
    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":infra:client"))
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jackson.core)
    testImplementation(libs.undertow.core)
}
