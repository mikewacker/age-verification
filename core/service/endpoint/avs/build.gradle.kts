plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:service:types:common"))
    api(project(":core:service:module:common"))
    api(project(":core:service:module:avs"))
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":base:data:crypto"))
    implementation(project(":core:data"))
    implementation(project(":base:api:base"))
    implementation(project(":core:api:types:common"))
    implementation(project(":core:service:types:avs"))
    implementation(project(":core:service:endpoint:common"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:data"))
    testFixturesApi(project(":core:api:types:common"))

    testFixturesImplementation(project(":core:service:types:common"))
    testFixturesImplementation(project(":core:service:endpoint:common"))
    testFixturesImplementation(project(":module:store:common:inmemory"))
    testFixturesImplementation(testFixtures(project(":module:store:avs:test")))
    testFixturesImplementation(testFixtures(project(":module:key:common:test")))
    testFixturesImplementation(testFixtures(project(":module:config:avs:test")))
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testImplementation(project(":base:data:crypto"))
    testImplementation(project(":core:data"))
    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types:common"))
    testImplementation(project(":module:extractor:common:builtin"))
    testImplementation(testFixtures(project(":base:api:base")))
}
