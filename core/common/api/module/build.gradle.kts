plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":base:api:adapter"))
    api(project(":base:api:base"))
    api(project(":core:common:api:types"))
    api("io.undertow:undertow-core")
}
