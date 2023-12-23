plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesApi("com.google.errorprone:error_prone_annotations")
    testFixturesApi("com.squareup.okhttp3:mockwebserver")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("org.junit.jupiter:junit-jupiter-api")

    // test
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("com.squareup.okhttp3:okhttp")
    testImplementation("io.undertow:undertow-core")
}
