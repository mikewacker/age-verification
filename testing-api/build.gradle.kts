plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("org.mockito:mockito-core")
}
