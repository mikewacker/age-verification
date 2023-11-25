plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":api"))
    api(project(":infra-api"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("com.squareup.okhttp3:okhttp")
    api("javax.inject:javax.inject")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":infra-api")))
    testImplementation(testFixtures(project(":testing-server")))
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("io.undertow:undertow-core")
}
