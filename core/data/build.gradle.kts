plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("org.immutables:value")

    api(project(":api-types"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.immutables:value-annotations")

    // test
    testImplementation(project(":api-types"))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("com.google.guava:guava-testlib")
}
