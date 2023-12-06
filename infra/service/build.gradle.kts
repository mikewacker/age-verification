plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":api-types"))
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":infra:client"))
    implementation("com.squareup.okhttp3:okhttp")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":api-types"))
    testImplementation(testFixtures(project(":api-types")))
    testImplementation(project(":infra:api"))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("com.google.dagger:dagger")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("io.undertow:undertow-core")
    testImplementation("javax.inject:javax.inject")
}
