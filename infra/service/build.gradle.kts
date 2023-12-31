plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    api(project(":infra:client"))
    api(libs.drift.api)
    api(libs.jackson.core)

    implementation(libs.okhttp3.okhttp)

    // test fixtures
    testFixturesApi(libs.undertow.core)

    testFixturesImplementation(project(":infra:api"))
    testFixturesImplementation(project(":infra:client"))
    testFixturesImplementation(testFixtures(project(":testing")))
    testFixturesImplementation(libs.drift.api)
    testFixturesImplementation(libs.jackson.core)
    testFixturesImplementation(libs.okhttp3.okhttp)

    // test
    testImplementation(project(":infra:client"))
    testImplementation(testFixtures(project(":testing")))
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jackson.core)
    testImplementation(libs.okhttp3.mockwebserver)
    testImplementation(libs.undertow.core)
}
