plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    api(project(":api"))
    api(project(":data"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("io.undertow:undertow-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":api"))
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("org.mockito:mockito-core")
    testFixturesApi("javax.inject:javax.inject")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api")))
    testImplementation("com.google.dagger:dagger")
    testImplementation("org.mockito:mockito-core")
    testImplementation("javax.inject:javax.inject")
}
