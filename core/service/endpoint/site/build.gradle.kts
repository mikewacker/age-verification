plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:api:module:common"))
    api(project(":core:service:module:common"))
    api(project(":core:service:module:site"))
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")

    implementation(project(":base:data:crypto"))
    implementation(project(":core:data"))
    implementation(project(":base:api:base"))
    implementation(project(":core:api:types:common"))
    implementation(project(":core:api:types:site"))
    implementation(project(":core:api:endpoint:site"))
    implementation(project(":infra:service"))
    implementation(project(":core:service:endpoint:common"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesImplementation("io.undertow:undertow-core")

    testFixturesImplementation(project(":base:data:crypto"))
    testFixturesImplementation(project(":core:data"))
    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(project(":core:api:types:common"))
    testFixturesImplementation(project(":core:api:types:avs"))
    testFixturesImplementation(project(":core:api:module:common"))
    testFixturesImplementation(project(":core:api:endpoint:site")) // Dagger component
    testFixturesImplementation(project(":core:api:endpoint:avs"))
    testFixturesImplementation(project(":core:service:module:common"))
    testFixturesImplementation(project(":infra:service"))
    testFixturesImplementation(project(":core:service:endpoint:common"))
    testFixturesImplementation(project(":module:extractor:common:builtin"))
    testFixturesImplementation(testFixtures(project(":module:extractor:common:test")))
    testFixturesImplementation(project(":module:store:common:inmemory"))
    testFixturesImplementation(testFixtures(project(":module:key:common:test")))
    testFixturesImplementation(testFixtures(project(":module:config:site:test")))
    testFixturesImplementation(testFixtures(project(":module:location:common:test")))
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testImplementation(project(":base:data:crypto"))
    testImplementation(project(":core:data"))
    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types:common"))
    testImplementation(project(":module:extractor:common:builtin"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("io.undertow:undertow-core")
}
