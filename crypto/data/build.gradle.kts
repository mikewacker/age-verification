plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.immutables.value)

    api(project(":base:data:json"))
    api(libs.immutables.annotations)
    api(libs.jackson.annotations)
    api(libs.jackson.databind)

    // test
    testImplementation(testFixtures(project(":base:data:json")))
    testImplementation(libs.guava.testlib)
    testImplementation(libs.jackson.core)
}
