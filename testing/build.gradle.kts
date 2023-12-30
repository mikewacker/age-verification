plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesApi(libs.errorprone.annotations)
    testFixturesApi(libs.junitJupiter.api)
    testFixturesApi(libs.okhttp3.mockwebserver)
    testFixturesApi(libs.undertow.core)

    // test
    testImplementation(libs.okhttp3.mockwebserver)
    testImplementation(libs.okhttp3.okhttp)
    testImplementation(libs.undertow.core)
}
