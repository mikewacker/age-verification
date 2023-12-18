plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:service:types:avs"))
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("javax.inject:javax.inject")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":core:service:types:avs"))
    testImplementation("com.google.dagger:dagger")
    testImplementation("javax.inject:javax.inject")
}
