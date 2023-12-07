plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesApi(project(":api:base"))
    testFixturesApi("com.fasterxml.jackson.core:jackson-core")
    testFixturesApi("com.google.errorprone:error_prone_annotations")
    testFixturesApi("com.squareup.okhttp3:mockwebserver")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("org.junit.jupiter:junit-jupiter-api")

    testFixturesImplementation(project(":infra:client"))
    testFixturesImplementation("com.squareup.okhttp3:okhttp")

    // test
    testImplementation(project(":api:base"))
    testImplementation(testFixtures(project(":api:base")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("io.undertow:undertow-core")
}
