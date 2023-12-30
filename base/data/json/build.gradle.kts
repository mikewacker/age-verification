plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    api(libs.immutables.annotations)
    api(libs.jackson.annotations)
    api(libs.jackson.core)
    api(libs.jackson.databind)

    // test fixtures
    testFixturesApi(libs.jackson.core)

    testFixturesImplementation(project(":base:data:json"))
    testFixturesImplementation(libs.assertj.core)

    // test
    testImplementation(libs.jackson.annotations)
    testImplementation(libs.jackson.core)
}
