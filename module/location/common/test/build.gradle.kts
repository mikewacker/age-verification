plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:common:service:module"))
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("javax.inject:javax.inject")

    testFixturesImplementation(testFixtures(project(":testing")))

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":core:common:service:module"))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.google.dagger:dagger")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("javax.inject:javax.inject")
}
