plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    implementation(project(":api"))
    implementation(project(":data"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("io.undertow:undertow-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesImplementation(project(":api"))
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("io.undertow:undertow-core")
    testFixturesImplementation("org.mockito:mockito-core")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api")))
    testImplementation("com.google.dagger:dagger")
    testImplementation("org.mockito:mockito-core")
    testImplementation("javax.inject:javax.inject")
}
