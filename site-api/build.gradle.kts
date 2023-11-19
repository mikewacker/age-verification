plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
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
}
