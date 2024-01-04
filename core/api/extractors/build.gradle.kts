plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":core:api:types"))
    api(libs.drift.api)
    api(libs.drift.drift)
    api(libs.undertow.core)
}
