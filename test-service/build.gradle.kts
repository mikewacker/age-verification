plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    annotationProcessor("com.google.dagger:dagger-compiler")

    implementation(project(":api"))
    implementation(project(":common-api"))
    implementation("com.google.dagger:dagger")
    implementation("io.undertow:undertow-core")
    implementation("javax.inject:javax.inject")

    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":testing-api"))
}
