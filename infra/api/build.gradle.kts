plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":api:base"))
    api(project(":api:adapter"))
    api("com.fasterxml.jackson.core:jackson-core")
    api("io.undertow:undertow-core")

    implementation(project(":api:data:json"))
    implementation("org.jboss.xnio:xnio-api")

    // test
    testImplementation(project(":api:base"))
    testImplementation(testFixtures(project(":api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("io.undertow:undertow-core")
}
