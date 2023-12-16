plugins {
    id("org.example.age.java-conventions")
    `java-test-fixtures`
}

dependencies {
    // test fixtures
    annotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:service:types:common"))
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("javax.inject:javax.inject")

    testFixturesImplementation(project(":base:data:crypto"))
    testFixturesImplementation(project(":core:data"))
    testFixturesImplementation(project(":core:api:types:common"))
    testFixturesImplementation(project(":module:store:common:inmemory"))

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":core:api:types:common"))
    testImplementation(project(":core:service:types:common"))
    testImplementation(project(":module:store:common:inmemory")) // Dagger component
    testImplementation("com.google.dagger:dagger")
    testImplementation("javax.inject:javax.inject")
}
