plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":base:api:base"))
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":base:data:json"))
    implementation(project(":infra:client"))
    implementation("com.squareup.okhttp3:okhttp")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi("io.undertow:undertow-core")

    testFixturesImplementation(project(":base:data:json"))
    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(project(":infra:api"))
    testFixturesImplementation(project(":infra:client"))
    testFixturesImplementation(testFixtures(project(":testing")))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-core")
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("com.squareup.okhttp3:okhttp")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testImplementation(project(":base:api:base"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("io.undertow:undertow-core")
}
