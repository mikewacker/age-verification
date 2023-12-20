plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesImplementation(project(":core:data"))
    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(project(":core:api:types"))
    testFixturesImplementation(testFixtures(project(":base:api:base")))
    testFixturesImplementation(testFixtures(project(":testing")))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-core")
    testFixturesImplementation("org.assertj:assertj-core")

    // test
    testImplementation(testFixtures(project(":core:service:endpoint")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("io.undertow:undertow-core")
}
