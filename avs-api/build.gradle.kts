plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
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
    implementation("io.undertow:undertow-core")
    implementation("javax.inject:javax.inject")
    implementation("org.immutables:value-annotations")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesImplementation(project(":api"))
    testFixturesImplementation(project(":common-api"))
    testFixturesImplementation(project(":common-service"))
    testFixturesImplementation(project(":data"))
    testFixturesImplementation(testFixtures(project(":common-api")))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-databind")
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":common-service"))
    testImplementation(testFixtures(project(":common-api")))
    testImplementation(testFixtures(project(":common-server")))
    testImplementation(testFixtures(project(":testing-server")))
    testImplementation("com.google.guava:guava")
    testImplementation("com.squareup.okhttp3:okhttp")
}
