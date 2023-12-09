plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("org.immutables:value")

    api(project(":api:base"))
    api(project(":api:data:json"))
    api(project(":api:data:crypto"))
    api(project(":core:common:api:types"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.immutables:value-annotations")

    // test
    testImplementation(project(":api:data:json"))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
}
