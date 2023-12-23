plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesApi(project(":base:api:base"))
    testFixturesApi(project(":infra:client"))
    testFixturesApi("com.fasterxml.jackson.core:jackson-core")
    testFixturesApi("com.google.errorprone:error_prone_annotations")
    testFixturesApi("com.squareup.okhttp3:mockwebserver")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("org.junit.jupiter:junit-jupiter-api")

    testFixturesImplementation(project(":base:data:json"))
    testFixturesImplementation("com.squareup.okhttp3:okhttp")

    // test
    testImplementation(project(":base:api:base"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("io.undertow:undertow-core")
}
