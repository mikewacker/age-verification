plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:common:api:module"))
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("javax.inject:javax.inject")

    testFixturesImplementation(project(":api:base"))
    testFixturesImplementation("io.undertow:undertow-core")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":api:base"))
    testImplementation(testFixtures(project(":api:base")))
    testImplementation(project(":core:common:api:module"))
    testImplementation("com.google.dagger:dagger")
    testImplementation("io.undertow:undertow-core")
    testImplementation("javax.inject:javax.inject")
    testImplementation("org.mockito:mockito-core")
}
