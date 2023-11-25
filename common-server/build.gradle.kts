plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api("com.google.dagger:dagger")
    api("com.google.guava:guava")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("com.google.guava:guava")
    testFixturesApi("io.undertow:undertow-core")
    testFixturesApi("javax.inject:javax.inject")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":testing-server")))
    testImplementation("com.squareup.okhttp3:okhttp")
}
