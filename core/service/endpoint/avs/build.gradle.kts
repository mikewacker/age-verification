plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:data"))
    testFixturesApi(project(":core:api:types:common"))

    testFixturesImplementation(project(":core:service:types:common"))
    testFixturesImplementation(project(":core:service:module:common"))
    testFixturesImplementation(project(":core:service:endpoint:common"))
    testFixturesImplementation(project(":module:store:common:inmemory"))
    testFixturesImplementation(testFixtures(project(":module:key:common:test")))
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("javax.inject:javax.inject")
}
