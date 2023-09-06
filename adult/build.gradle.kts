plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    annotationProcessor("com.google.dagger:dagger-compiler:2.47")

    implementation(project(":common"))
    implementation("com.google.dagger:dagger:2.47")
    implementation("io.undertow:undertow-core:2.3.7.Final")

    testAnnotationProcessor("com.google.dagger:dagger-compiler:2.47")

    testImplementation(project(":testing"))
    testImplementation("com.squareup.okhttp3:okhttp:4.11.0")
}
