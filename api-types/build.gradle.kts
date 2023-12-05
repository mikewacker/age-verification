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
    api("org.jboss.xnio:xnio-api")

    // test fixtures
    testFixturesApi("org.jboss.xnio:xnio-api")

    testFixturesImplementation("org.assertj:assertj-core")
    testFixturesImplementation("org.mockito:mockito-core")

    // test
    testImplementation("com.google.guava:guava-testlib")
}
