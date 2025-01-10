plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(project(":service:api"))
    api(libs.dagger.dagger)
    api(libs.jackson.databind)
    implementation(libs.guava.guava)
    implementation(libs.jakartaInject.api)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
}
