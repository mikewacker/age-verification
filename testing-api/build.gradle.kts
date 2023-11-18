plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":api"))
    implementation("io.undertow:undertow-core")
    implementation("org.assertj:assertj-core")
    implementation("org.jboss.xnio:xnio-api")
    implementation("org.mockito:mockito-core")
}
