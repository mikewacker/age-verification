plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(libs.drift.api)

    // test
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.mockito.core)
}
