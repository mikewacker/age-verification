plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":base:api:base"))
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.squareup.okhttp3:okhttp")

    implementation(project(":base:data:json"))

    // test
    testImplementation(project(":base:api:base"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(testFixtures(project(":testing")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("com.squareup.okhttp3:okhttp")
    testImplementation("com.squareup.okio:okio-jvm")
}
