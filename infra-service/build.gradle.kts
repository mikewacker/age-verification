plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    annotationProcessor("com.google.dagger:dagger-compiler")

    implementation(project(":api"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.google.dagger:dagger")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("javax.inject:javax.inject")

    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":infra-api"))
    testImplementation(project(":testing-server"))
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("io.undertow:undertow-core")
}
