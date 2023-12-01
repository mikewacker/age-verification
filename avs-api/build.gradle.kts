plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":api"))
    api(project(":common-api"))
    api(project(":data"))
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")

    api(project(":infra-api"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("javax.inject:javax.inject")

    testFixturesApi(project(":api"))
    testFixturesApi(project(":common-api"))
    testFixturesApi(project(":common-extractor-builtin"))
    testFixturesApi(testFixtures(project(":common-extractor-builtin")))
    testFixturesApi(project(":data"))

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":testing-server")))
}
