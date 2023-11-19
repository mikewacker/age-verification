plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    implementation(project(":api"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("io.undertow:undertow-core")
    implementation("org.jboss.xnio:xnio-api")

    // test
    testImplementation(testFixtures(project(":testing-server")))
    testImplementation("com.squareup.okhttp3:okhttp")
}
