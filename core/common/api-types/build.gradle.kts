plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    annotationProcessor("org.immutables:value")

    api(project(":api-types"))
    api(project(":core:data"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("io.undertow:undertow-core")
    api("org.immutables:value-annotations")
}
