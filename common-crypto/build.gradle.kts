plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":api"))
    api(project(":data"))
    api(project(":common-api"))
    api(project(":infra-api"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")
    testAnnotationProcessor("org.immutables:value")

    testImplementation(project(":common-data"))
    testImplementation(testFixtures(project(":api")))
    testImplementation("org.immutables:value-annotations")
}
