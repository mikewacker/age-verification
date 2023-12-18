plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesImplementation("org.assertj:assertj-core")

    // test
    testImplementation("com.google.guava:guava-testlib")
}
