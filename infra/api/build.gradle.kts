plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":base:api:base"))
    api(project(":base:api:adapter"))
    api("com.fasterxml.jackson.core:jackson-core")
    api("io.undertow:undertow-core")

    implementation(project(":base:data:json"))
    implementation("org.jboss.xnio:xnio-api")

    // test
    testImplementation(project(":base:api:base"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("io.undertow:undertow-core")
}
