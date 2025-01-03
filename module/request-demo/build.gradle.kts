plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(project(":service"))
    api(libs.dagger.dagger)
    implementation(libs.jakartaInject.api)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
    testImplementation(libs.dropwizard.core)
    testImplementation(libs.dropwizard.testing)
    testImplementation(libs.jaxRs.api)
    testImplementation(libs.okhttp.okhttp)
}
