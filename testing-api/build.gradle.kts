plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi("com.fasterxml.jackson.core:jackson-databind")
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("javax.inject:javax.inject")
    testFixturesApi("org.mockito:mockito-core")
}
