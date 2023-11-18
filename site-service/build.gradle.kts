plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    compileOnly("org.immutables:value-annotations")
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    implementation(project(":api"))
    implementation(project(":common-api"))
    implementation(project(":common-service"))
    implementation(project(":data"))
    implementation(project(":infra-service"))
    implementation(project(":site-api"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.google.dagger:dagger")
    implementation("com.google.guava:guava")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("javax.inject:javax.inject")
    implementation("org.jboss.xnio:xnio-api")

    testCompileOnly("org.immutables:value-annotations")
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":avs-api"))
    testImplementation(project(":common-server"))
    testImplementation(project(":common-service"))
    testImplementation(project(":test-server"))
    testImplementation(project(":test-service"))
    testImplementation(project(":testing-api"))
    testImplementation(project(":testing-server"))
    testImplementation("io.undertow:undertow-core")
}
