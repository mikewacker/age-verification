plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    api(project(":common-api"))
    api(project(":infra-api"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")
    api("org.immutables:value-annotations")

    // test fixtures
    annotationProcessor("com.google.dagger:dagger-compiler")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":testing-api")))
}
