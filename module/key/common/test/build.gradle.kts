plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:service:module:common"))
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("javax.inject:javax.inject")

    testFixturesImplementation(project(":crypto:data"))

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:service:module:common"))
    testImplementation("com.google.dagger:dagger")
    testImplementation("javax.inject:javax.inject")
}
