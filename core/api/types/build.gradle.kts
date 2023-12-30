plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.immutables.value)

    api(project(":base:data:json"))
    api(project(":crypto:data"))
    api(project(":core:data"))
    api(project(":base:api:base"))
    api(libs.immutables.annotations)
    api(libs.jackson.annotations)
    api(libs.jackson.databind)

    // test
    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(testFixtures(project(":base:data:json")))
    testImplementation(libs.jackson.core)
}
