plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    api(project(":base:api:base"))
    api(project(":base:api:adapter"))
    api(libs.jackson.core)
    api(libs.undertow.core)

    implementation(project(":base:data:json"))
    implementation(libs.xnio.api)

    // test fixtures
    testFixturesApi(libs.undertow.core)

    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(libs.jackson.core)

    // test
    testImplementation(project(":base:api:base"))
    testImplementation(project(":infra:client"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation(libs.jackson.core)
    testImplementation(libs.undertow.core)
}
