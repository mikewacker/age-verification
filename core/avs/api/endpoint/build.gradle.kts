plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:avs:api:types"))
    api(project(":core:common:api:module"))
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")

    implementation(project(":base:api:adapter"))
    implementation(project(":base:api:base"))
    implementation(project(":base:data:crypto"))
    implementation(project(":core:common:api:types"))
    implementation(project(":core:data"))
    implementation(project(":infra:api"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi("io.undertow:undertow-core")

    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(project(":base:data:crypto"))
    testFixturesImplementation(project(":core:avs:api:types"))
    testFixturesImplementation(project(":core:common:api:module"))
    testFixturesImplementation(project(":core:common:api:types"))
    testFixturesImplementation(project(":core:data"))
    testFixturesImplementation(project(":module:extractor:common:builtin"))
    testFixturesImplementation(testFixtures(project(":module:extractor:common:test")))
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testImplementation(project(":base:api:base"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(project(":base:data:crypto"))
    testImplementation(project(":core:common:api:types"))
    testImplementation(project(":core:data"))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("io.undertow:undertow-core")
}
