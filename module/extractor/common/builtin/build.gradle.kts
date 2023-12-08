plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    api(project(":api:base"))
    api(project(":core:common:api:module"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")
    api("org.immutables:value-annotations")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:common:api:types"))
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("javax.inject:javax.inject")

    testFixturesImplementation(project(":api:base"))
    testFixturesImplementation("io.undertow:undertow-core")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":api:base"))
    testImplementation(testFixtures(project(":api:base")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("io.undertow:undertow-core")
    testImplementation("org.mockito:mockito-core")
}
