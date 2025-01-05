plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    api(project(":api"))
    api(project(":service:api"))
    api(libs.dagger.dagger)
    api(libs.immutables.annotations)
    api(libs.jackson.annotations)
    api(libs.jackson.databind)
    implementation(libs.jakartaInject.api)
    implementation(libs.okhttp.okhttp)
    implementation(libs.retrofit.retrofit)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
    testImplementation(libs.dropwizard.core)
    testImplementation(libs.dropwizard.testing)
}
