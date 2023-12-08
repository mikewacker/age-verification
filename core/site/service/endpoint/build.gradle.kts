plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    api(project(":api:base"))
    api(project(":core:common:api:module"))
    api(project(":core:common:api:types"))
    api(project(":core:common:service:endpoint"))
    api(project(":core:common:service:module"))
    api(project(":core:site:api:endpoint"))
    api(project(":core:site:service:module"))
    api(project(":core:data"))
    api(project(":infra:service"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("com.google.guava:guava")
    api("javax.inject:javax.inject")
    api("org.immutables:value-annotations")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesApi(project(":core:common:api:types"))
    testFixturesApi(project(":core:data"))
    testFixturesApi("com.google.dagger:dagger")
    testFixturesApi("javax.inject:javax.inject")
    testFixturesApi("io.undertow:undertow-core")

    testFixturesImplementation(project(":api:base"))
    testFixturesApi(project(":core:avs:api:endpoint"))
    testFixturesImplementation(project(":core:common:api:module"))
    testFixturesImplementation(project(":core:common:service:module"))
    testFixturesApi(project(":core:common:service:endpoint"))
    testFixturesApi(project(":infra:service"))
    testFixturesApi(testFixtures(project(":module:config:common:test")))
    testFixturesApi(testFixtures(project(":module:config:site:test")))
    testFixturesApi(project(":module:extractor:common:builtin"))
    testFixturesApi(testFixtures(project(":module:extractor:common:builtin")))
    testFixturesApi(testFixtures(project(":module:key:common:test")))
    testFixturesApi(project(":module:store:common:inmemory"))
    testFixturesImplementation("com.fasterxml.jackson.core:jackson-databind")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("io.undertow:undertow-core")
}
