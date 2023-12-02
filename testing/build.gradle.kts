plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesApi(project(":api"))
    testFixturesApi("com.fasterxml.jackson.core:jackson-core")
    testFixturesApi("com.squareup.okhttp3:mockwebserver")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("org.junit.jupiter:junit-jupiter-api")

    testFixturesImplementation(project(":infra:client"))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-databind")
    testFixturesImplementation("com.fasterxml.jackson.datatype:jackson-datatype-guava")
    testFixturesImplementation("com.squareup.okhttp3:okhttp")

    // test
    testImplementation(testFixtures(project(":api")))
}
