plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    api(project(":api"))
    api(project(":common-api"))
    api(project(":data"))
    api(project(":infra-api"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")
    api("org.immutables:value-annotations")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":api"))
    testFixturesApi(project(":common-api"))
    testFixturesApi(project(":common-service"))
    testFixturesApi(project(":data"))
    testFixturesApi(testFixtures(project(":common-api")))
    testFixturesApi("com.fasterxml.jackson.core:jackson-databind")
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("javax.inject:javax.inject")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":common-service"))
    testImplementation(testFixtures(project(":common-api")))
    testImplementation(testFixtures(project(":common-server")))
    testImplementation(testFixtures(project(":testing-server")))
}
