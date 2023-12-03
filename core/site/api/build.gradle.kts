plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":api-types"))
    api(project(":core:common:api-types"))
    api(project(":core:data"))
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")

    api(project(":core:common:api"))
    api(project(":infra:api"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("javax.inject:javax.inject")

    testFixturesImplementation(project(":api-types"))
    testFixturesImplementation(project(":core:common:api-types"))
    testFixturesImplementation(project(":core:data"))
    testFixturesApi(project(":module:extractor:common:builtin"))
    testFixturesApi(testFixtures(project(":module:extractor:common:builtin")))

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api-types")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("io.undertow:undertow-core")
}