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
    implementation(project(":common-service"))
    implementation(project(":data"))
    implementation(project(":infra-api"))
    implementation(project(":infra-service"))
    implementation(project(":site-api"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.google.dagger:dagger")
    implementation("com.google.guava:guava")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("javax.inject:javax.inject")
    implementation("org.immutables:value-annotations")
    implementation("org.jboss.xnio:xnio-api")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesImplementation(project(":api"))
    testFixturesImplementation(project(":avs-api"))
    testFixturesImplementation(project(":common-api"))
    testFixturesImplementation(project(":common-service"))
    testFixturesImplementation(project(":data"))
    testFixturesImplementation(project(":infra-service"))
    testFixturesImplementation(testFixtures(project(":common-api")))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-databind")
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("com.squareup.okhttp3:okhttp")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":avs-api"))
    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":common-api")))
    testImplementation(testFixtures(project(":common-server")))
    testImplementation(testFixtures(project(":testing-server")))
    testImplementation("io.undertow:undertow-core")
}
