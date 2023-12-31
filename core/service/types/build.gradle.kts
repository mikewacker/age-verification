plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.immutables.value)

    api(project(":crypto:data"))
    api(project(":core:data"))
    api(project(":core:api:types"))
    api(libs.drift.api)
    api(libs.errorprone.annotations)
    api(libs.immutables.annotations)
    api(libs.jackson.annotations)
    api(libs.jackson.core)
    api(libs.jackson.databind)

    // test
    testImplementation(project(":core:data"))
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jackson.core)
}
