plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(libs.drift.api)
    api(libs.jackson.core)
    api(libs.okhttp3.okhttp)

    // test
    testImplementation(testFixtures(project(":testing")))
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jackson.core)
    testImplementation(libs.okhttp3.mockwebserver)
    testImplementation(libs.okhttp3.okhttp)
    testImplementation(libs.okio.jvm)
}
