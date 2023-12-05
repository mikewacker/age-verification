plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("org.immutables:value")

    api(project(":api-types"))
    api(project(":core:common:service-types"))
    api(project(":core:data"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.immutables:value-annotations")

    implementation("com.google.guava:guava")

    // test
    testImplementation("com.fasterxml.jackson.core:jackson-core")
}
