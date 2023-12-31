plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.immutables.value)

    api(project(":crypto:data"))
    api(project(":core:data"))
    api(libs.drift.api)
    api(libs.immutables.annotations)
    api(libs.jackson.annotations)
    api(libs.jackson.databind)

    // test
    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jackson.core)
}
