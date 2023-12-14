plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("org.immutables:value")

    api(project(":base:api:base"))
    api(project(":base:data:json"))
    api(project(":base:data:crypto"))
    api(project(":core:common:api:types"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.immutables:value-annotations")

    // test
    testImplementation(project(":base:data:crypto"))
    testImplementation(project(":base:data:json"))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
}
