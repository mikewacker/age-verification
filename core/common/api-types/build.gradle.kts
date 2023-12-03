plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    api(project(":api"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("io.undertow:undertow-core")
}
