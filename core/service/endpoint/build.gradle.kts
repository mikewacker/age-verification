plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)

    api(project(":core:api:extractors"))
    api(project(":core:service:types"))
    api(libs.dagger.compiler)
    api(libs.javaxInject.inject)
    api(libs.undertow.core)

    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":core:api:types"))
    implementation(project(":core:api:endpoint"))
    implementation(project(":infra:client"))
    implementation(project(":infra:service"))
    implementation(project(":core:service:crypto"))
    implementation(libs.drift.api)
    implementation(libs.jackson.core)

    // test fixtures
    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(project(":crypto:data"))
    testFixturesApi(project(":core:data"))
    testFixturesApi(project(":core:api:types"))
    testFixturesApi(libs.undertow.core)

    testFixturesImplementation(project(":core:api:endpoint"))
    testFixturesImplementation(project(":core:service:types"))
    testFixturesImplementation(project(":infra:client"))
    testFixturesImplementation(project(":infra:service"))
    testFixturesImplementation(project(":core:service:crypto"))
    testFixturesImplementation(project(":module:extractor:builtin"))
    testFixturesImplementation(testFixtures(project(":module:extractor:test")))
    testFixturesImplementation(project(":module:store:inmemory"))
    testFixturesImplementation(testFixtures(project(":module:service:test")))
    testFixturesImplementation(libs.dagger.dagger)
    testFixturesImplementation(libs.drift.api)
    testFixturesImplementation(libs.jackson.core)
    testFixturesImplementation(libs.javaxInject.inject)

    // test
    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":module:extractor:builtin"))
    testImplementation(project(":infra:client"))
    testImplementation(testFixtures(project(":testing")))
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.undertow.core)
}
