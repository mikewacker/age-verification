plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":api-types"))
    api("com.fasterxml.jackson.core:jackson-core")
    api("io.undertow:undertow-core")
    api("org.jboss.xnio:xnio-api")

    // test
    testImplementation(project(":api-types"))
    testImplementation(testFixtures(project(":api-types")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("io.undertow:undertow-core")
}
