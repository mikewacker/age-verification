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

    testFixturesImplementation(project(":api:data:crypto"))

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":api:data:crypto"))
    testImplementation(project(":core:common:service:module"))
    testImplementation("com.google.dagger:dagger")
    testImplementation("javax.inject:javax.inject")
}
