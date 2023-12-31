plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    api(project(":base:api:adapter"))
    api(libs.drift.api)
    api(libs.jackson.core)
    api(libs.undertow.core)

    implementation(libs.xnio.api)

    // test fixtures
    testFixturesApi(libs.undertow.core)

    testFixturesImplementation(libs.drift.api)
    testFixturesImplementation(libs.jackson.core)

    // test
    testImplementation(project(":infra:client"))
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jackson.core)
    testImplementation(libs.undertow.core)
}
