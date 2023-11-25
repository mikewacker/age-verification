plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesApi(project(":data"))
    testFixturesApi("com.fasterxml.jackson.core:jackson-databind")
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("com.google.guava:guava")
    testFixturesApi("com.squareup.okhttp3:mockwebserver")
    testFixturesApi("com.squareup.okhttp3:okhttp")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("org.junit.jupiter:junit-jupiter-api")

    // test
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("com.google.guava:guava")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("com.squareup.okhttp3:okhttp")
    testImplementation("io.undertow:undertow-core")
}
