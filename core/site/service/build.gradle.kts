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
    api(project(":core:common:api"))
    api(project(":core:common:service"))
    api(project(":core:site:api"))
    api(project(":core:data"))
    api(project(":infra:service"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("com.google.guava:guava")
    api("javax.inject:javax.inject")
    api("org.immutables:value-annotations")
    api("org.jboss.xnio:xnio-api")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:avs:api"))
    testFixturesApi(testFixtures(project(":module:config:common:test")))
    testFixturesApi(project(":module:extractor:common:builtin"))
    testFixturesApi(testFixtures(project(":module:extractor:common:builtin")))
    testFixturesApi(testFixtures(project(":module:key:common:test")))
    testFixturesApi(testFixtures(project(":testing")))

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":testing")))
}
