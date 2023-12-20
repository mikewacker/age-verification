plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("org.immutables:value")

    api(project(":base:data:json"))
    api(project(":crypto:data"))
    api(project(":core:data"))
    api(project(":base:api:base"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.immutables:value-annotations")

    // test
    testImplementation(project(":base:data:json"))
    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(testFixtures(project(":base:data:json")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
}
