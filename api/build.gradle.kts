plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.jboss.xnio:xnio-api")

    // test fixtures
    testFixturesApi("org.assertj:assertj-core")
    testFixturesApi("org.jboss.xnio:xnio-api")
    testFixturesApi("org.mockito:mockito-core")

    // test
    testImplementation("com.google.guava:guava-testlib")
}
