plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:common:api:module"))
    api(project(":core:common:service:module"))
    api(project(":core:site:service:module"))
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")

    implementation(project(":base:api:base"))
    implementation(project(":base:data:crypto"))
    implementation(project(":core:common:api:types"))
    implementation(project(":core:common:service:endpoint"))
    implementation(project(":core:data"))
    implementation(project(":core:site:api:endpoint"))
    implementation(project(":core:site:api:types"))
    implementation(project(":infra:service"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    testFixturesImplementation("io.undertow:undertow-core")

    testFixturesImplementation(project(":base:api:base"))
    testFixturesImplementation(project(":base:data:crypto"))
    testFixturesImplementation(project(":core:avs:api:endpoint"))
    testFixturesImplementation(project(":core:avs:api:types"))
    testFixturesImplementation(project(":core:common:api:types"))
    testFixturesImplementation(project(":core:common:service:module"))
    testFixturesImplementation(project(":core:common:service:endpoint"))
    testFixturesImplementation(project(":core:data"))
    testFixturesImplementation(project(":core:site:api:endpoint"))
    testFixturesImplementation(project(":infra:service"))
    testFixturesImplementation(testFixtures(project(":module:config:site:test")))
    testFixturesImplementation(project(":module:extractor:common:builtin"))
    testFixturesImplementation(testFixtures(project(":module:extractor:common:test")))
    testFixturesImplementation(testFixtures(project(":module:key:common:test")))
    testFixturesImplementation(testFixtures(project(":module:location:common:test")))
    testFixturesImplementation(project(":module:store:common:inmemory"))
    testFixturesImplementation("com.google.dagger:dagger")
    testFixturesImplementation("javax.inject:javax.inject")

    // test
    testImplementation(project(":base:api:base"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(project(":base:data:crypto"))
    testImplementation(project(":core:common:api:types"))
    testImplementation(project(":core:data"))
    testImplementation(project(":module:extractor:common:builtin"))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("io.undertow:undertow-core")
}
