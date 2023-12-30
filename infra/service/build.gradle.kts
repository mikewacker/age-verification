plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    api(project(":base:api:base"))
    api(project(":infra:client"))
    api(libs.jackson.core)

    implementation(libs.okhttp3.okhttp)

    // test fixtures
    testFixturesApi(libs.undertow.core)

    testFixturesImplementation(project(":base:data:json"))
    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(project(":infra:api"))
    testFixturesImplementation(project(":infra:client"))
    testFixturesImplementation(testFixtures(project(":testing")))
    testFixturesImplementation(libs.jackson.core)
    testFixturesImplementation(libs.okhttp3.okhttp)

    // test
    testImplementation(project(":base:api:base"))
    testImplementation(project(":infra:client"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation(libs.jackson.core)
    testImplementation(libs.okhttp3.mockwebserver)
    testImplementation(libs.undertow.core)
}
