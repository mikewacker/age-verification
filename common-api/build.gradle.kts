plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    api(project(":api"))
    api(project(":data"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("io.undertow:undertow-core")
}
