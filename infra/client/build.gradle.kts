plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":base:api:base"))
    api(libs.jackson.core)
    api(libs.okhttp3.okhttp)

    implementation(project(":base:data:json"))

    // test
    testImplementation(project(":base:api:base"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation(libs.jackson.core)
    testImplementation(libs.okhttp3.mockwebserver)
    testImplementation(libs.okhttp3.okhttp)
    testImplementation(libs.okio.jvm)
}
