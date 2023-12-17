plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    testFixturesImplementation(project(":base:data:crypto"))
    testFixturesImplementation(project(":core:data"))
    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(project(":core:api:types:common"))
    testFixturesImplementation(testFixtures(project(":base:api:base")))
    testFixturesImplementation(testFixtures(project(":testing")))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-core")
    testFixturesImplementation("org.assertj:assertj-core")
}
