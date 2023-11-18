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
    implementation(project(":site-api"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.google.dagger:dagger")
    implementation("com.google.guava:guava")
    implementation("javax.inject:javax.inject")
    implementation("org.jboss.xnio:xnio-api")

    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":testing-api"))
}
