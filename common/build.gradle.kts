plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    annotationProcessor("com.google.dagger:dagger-compiler:2.47")

    implementation(project(":data"))
    implementation("com.google.dagger:dagger:2.47")
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("io.undertow:undertow-core:2.3.8.Final")
    implementation("javax.inject:javax.inject:1")

    testAnnotationProcessor("com.google.dagger:dagger-compiler:2.47")

    testImplementation(project(":testing"))
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")
}
