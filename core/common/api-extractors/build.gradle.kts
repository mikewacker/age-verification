plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    api(project(":api-types"))
    api(project(":core:common:api-types"))
    api("io.undertow:undertow-core")
}