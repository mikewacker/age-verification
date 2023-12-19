plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:api:extractors:common"))
    api(project(":core:service:types:common"))
    api(project(":core:service:types:site"))
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")

    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":base:api:base"))
    implementation(project(":core:api:types:common"))
    implementation(project(":core:api:types:site"))
    implementation(project(":core:api:endpoint:site"))
    implementation(project(":infra:service"))
    implementation(project(":core:service:crypto:common"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":crypto:data"))
    testFixturesApi(project(":core:data"))
    testFixturesApi(project(":core:api:types:common"))
    testFixturesApi("io.undertow:undertow-core")

    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(project(":core:api:types:avs"))
    testFixturesImplementation(project(":core:api:endpoint:site")) // Dagger component
    testFixturesImplementation(project(":core:api:endpoint:avs"))
    testFixturesImplementation(project(":core:service:types:common"))
    testFixturesImplementation(project(":infra:service"))
    testFixturesImplementation(project(":core:service:crypto:common"))
    testFixturesImplementation(project(":module:extractor:builtin:common"))
    testFixturesImplementation(testFixtures(project(":module:extractor:test:common")))
    testFixturesImplementation(project(":module:store:inmemory:common"))
    testFixturesImplementation(testFixtures(project(":module:service:test:common")))
    testFixturesImplementation(testFixtures(project(":module:service:test:site")))
    testFixturesImplementation(testFixtures(project(":module:service:test:avs")))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-core")
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types:common"))
    testImplementation(project(":module:extractor:builtin:common"))
    testImplementation(testFixtures(project(":core:integration-test")))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("io.undertow:undertow-core")
}
