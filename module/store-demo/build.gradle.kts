plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    api(project(":api"))
    api(project(":service:module"))
    api(libs.dagger.dagger)
    api(libs.immutables.annotations)
    api(libs.jackson.annotations)
    api(libs.jackson.databind)
    implementation(libs.guava.guava)
    implementation(libs.jakartaInject.api)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
}
