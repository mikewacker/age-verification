plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":base:api:base"))
    api(project(":core:api:types"))
    api(project(":base:api:adapter"))
    api("io.undertow:undertow-core")
}
