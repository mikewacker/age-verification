plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
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
    implementation("org.immutables:value-annotations")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")
    testAnnotationProcessor("org.immutables:value")

    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":common-api")))
    testImplementation(testFixtures(project(":infra-api")))
}
