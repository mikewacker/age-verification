plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":core:api:types"))
    api(project(":base:api:adapter"))
    api(libs.drift.api)
    api(libs.undertow.core)
}
