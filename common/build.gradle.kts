plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    api(project(":api"))
    api(project(":common-api"))
    api(project(":common-service"))
    api(project(":data"))
    api(project(":infra-api"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("com.google.guava:guava")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")
    api("org.immutables:value-annotations")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":common-service"))
    testImplementation(testFixtures(project(":common-api")))
    testImplementation("org.mockito:mockito-core")
}
