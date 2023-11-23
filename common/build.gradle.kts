plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    implementation(project(":api"))
    implementation(project(":avs-api"))
    implementation(project(":common-api"))
    implementation(project(":common-service"))
    implementation(project(":data"))
    implementation(project(":infra-api"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.google.dagger:dagger")
    implementation("com.google.guava:guava")
    implementation("io.undertow:undertow-core")
    implementation("javax.inject:javax.inject")
    implementation("org.immutables:value-annotations")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":common-service"))
    testImplementation(testFixtures(project(":common-api")))
    testImplementation("org.mockito:mockito-core")
}
