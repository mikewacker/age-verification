plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesApi("com.fasterxml.jackson.core:jackson-databind")
    testFixturesApi("com.fasterxml.jackson.datatype:jackson-datatype-guava")
    testFixturesApi("com.google.guava:guava")
    testFixturesApi("com.squareup.okhttp3:mockwebserver")
    testFixturesApi("com.squareup.okhttp3:okhttp")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("org.junit.jupiter:junit-jupiter-api")
}
