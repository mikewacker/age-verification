plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:service:types:common"))
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("javax.inject:javax.inject")

    testFixturesImplementation(project(":crypto:data"))
    testFixturesImplementation(testFixtures(project(":testing")))

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:service:types:common"))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.google.dagger:dagger")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("javax.inject:javax.inject")
}
