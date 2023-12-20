plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.immutables:value-annotations")

    // test fixtures
    testFixturesApi("com.fasterxml.jackson.core:jackson-core")

    testFixturesImplementation(project(":base:data:json"))
    testFixturesImplementation("org.assertj:assertj-core")

    // test
    testImplementation("com.fasterxml.jackson.core:jackson-annotations")
    testImplementation("com.fasterxml.jackson.core:jackson-core")
}
