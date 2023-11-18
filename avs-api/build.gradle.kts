plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    compileOnly("org.immutables:value-annotations")
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    implementation(project(":api"))
    implementation(project(":common-api"))
    implementation(project(":data"))
    implementation(project(":infra-api"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.google.dagger:dagger")
    implementation("com.google.guava:guava")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("io.undertow:undertow-core")
    implementation("javax.inject:javax.inject")

    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":common-server"))
    testImplementation(project(":common-service"))
    testImplementation(project(":test-server"))
    testImplementation(project(":test-service"))
    testImplementation(project(":testing-server"))
}
