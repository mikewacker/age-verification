plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:api:extractors"))
    api(project(":core:service:types"))
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")

    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":base:api:base"))
    implementation(project(":core:api:types"))
    implementation(project(":core:api:endpoint"))
    implementation(project(":infra:client"))
    implementation(project(":infra:service"))
    implementation(project(":core:service:crypto"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":crypto:data"))
    testFixturesApi(project(":core:data"))
    testFixturesApi(project(":core:api:types"))
    testFixturesApi("io.undertow:undertow-core")

    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(project(":core:api:endpoint"))
    testFixturesImplementation(project(":core:service:types"))
    testFixturesImplementation(project(":infra:client"))
    testFixturesImplementation(project(":infra:service"))
    testFixturesImplementation(project(":core:service:crypto"))
    testFixturesImplementation(project(":module:extractor:builtin"))
    testFixturesImplementation(testFixtures(project(":module:extractor:test")))
    testFixturesImplementation(project(":module:store:inmemory"))
    testFixturesImplementation(testFixtures(project(":module:service:test")))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-core")
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":module:extractor:builtin"))
    testImplementation(project(":infra:client"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("io.undertow:undertow-core")
}
