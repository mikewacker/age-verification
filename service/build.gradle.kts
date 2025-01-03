plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    api(project(":api"))
    api(libs.dagger.dagger)
    api(libs.immutables.annotations)
    api(libs.jackson.databind)
    implementation(libs.jakartaInject.api)
    implementation(libs.jaxRs.api)
    implementation(libs.retrofit.retrofit)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
    testImplementation(libs.dropwizard.core) // also provides RuntimeDelegate for JAX-RS Response
    testImplementation(libs.dropwizard.testing)
    testImplementation(libs.okhttp.okhttp)
    testImplementation(libs.retrofit.mock)
}
