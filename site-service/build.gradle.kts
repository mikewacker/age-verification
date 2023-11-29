plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    api(project(":api"))
    api(project(":common-api"))
    api(project(":common-crypto"))
    api(project(":common-data"))
    api(project(":common-store"))
    api(project(":data"))
    api(project(":infra-service"))
    api(project(":site-api"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("com.google.guava:guava")
    api("javax.inject:javax.inject")
    api("org.immutables:value-annotations")
    api("org.jboss.xnio:xnio-api")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":avs-api"))
    testFixturesApi(testFixtures(project(":common-crypto")))
    testFixturesApi(testFixtures(project(":common-data")))
    testFixturesApi(testFixtures(project(":testing-server")))

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api")))
    testImplementation("com.squareup.okhttp3:okhttp")
    testImplementation("io.undertow:undertow-core")
}
