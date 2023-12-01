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
    api("javax.inject:javax.inject")

    implementation(project(":infra-client"))
    implementation("com.squareup.okhttp3:okhttp")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":testing-server")))
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("io.undertow:undertow-core")
}
