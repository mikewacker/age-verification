plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesImplementation(project(":data"))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-databind")
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("com.google.guava:guava")
    testFixturesImplementation("com.squareup.okhttp3:mockwebserver")
    testFixturesImplementation("com.squareup.okhttp3:okhttp")
    testFixturesImplementation("io.undertow:undertow-core")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api")

    // test
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("com.google.guava:guava")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("com.squareup.okhttp3:okhttp")
    testImplementation("io.undertow:undertow-core")
}
