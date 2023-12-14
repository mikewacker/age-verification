plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:api:types:site"))
    api(project(":core:api:module:common"))
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")

    implementation(project(":core:data"))
    implementation(project(":base:api:base"))
    implementation(project(":core:api:types:common"))
    implementation(project(":base:api:adapter"))
    implementation(project(":infra:api"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi("io.undertow:undertow-core")

    testFixturesImplementation(project(":core:data"))
    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(project(":core:api:types:common"))
    testFixturesImplementation(project(":core:api:types:site"))
    testFixturesImplementation(project(":core:api:module:common"))
    testFixturesImplementation(project(":module:extractor:common:builtin"))
    testFixturesImplementation(testFixtures(project(":module:extractor:common:test")))
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testImplementation(project(":base:data:crypto"))
    testImplementation(project(":core:data"))
    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types:common"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("io.undertow:undertow-core")
}