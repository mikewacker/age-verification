plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesApi(project(":api-types"))
    testFixturesApi("com.fasterxml.jackson.core:jackson-core")
    testFixturesApi("com.google.errorprone:error_prone_annotations")
    testFixturesApi("com.squareup.okhttp3:mockwebserver")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("org.junit.jupiter:junit-jupiter-api")

    testFixturesImplementation(project(":infra:client"))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-databind")
    testFixturesImplementation("com.fasterxml.jackson.datatype:jackson-datatype-guava")
    testFixturesImplementation("com.squareup.okhttp3:okhttp")

    // test
    testImplementation(project(":api-types"))
    testImplementation(testFixtures(project(":api-types")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("io.undertow:undertow-core")
}
