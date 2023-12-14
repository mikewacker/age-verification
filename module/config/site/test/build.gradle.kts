plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:site:service:module"))
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("javax.inject:javax.inject")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":core:site:service:module"))
    testImplementation("com.google.dagger:dagger")
    testImplementation("javax.inject:javax.inject")
}
