plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.jboss.xnio:xnio-api")

    // test fixtures
    testFixturesImplementation("org.assertj:assertj-core")
    testFixturesImplementation("org.jboss.xnio:xnio-api")
    testFixturesImplementation("org.mockito:mockito-core")

    // test
    testImplementation("com.google.guava:guava-testlib")
}
