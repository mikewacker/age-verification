plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    annotationProcessor("com.google.dagger:dagger-compiler")

    implementation("com.google.dagger:dagger")
    implementation("com.google.guava:guava")
    implementation("io.undertow:undertow-core")
    implementation("javax.inject:javax.inject")

    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":testing-server"))
    testImplementation("com.squareup.okhttp3:okhttp")
}
