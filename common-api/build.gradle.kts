plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":api"))
    implementation(project(":data"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("io.undertow:undertow-core")

    testCompileOnly("org.immutables:value-annotations")
    testAnnotationProcessor("org.immutables:value")

    testImplementation(project(":testing-api"))
}
