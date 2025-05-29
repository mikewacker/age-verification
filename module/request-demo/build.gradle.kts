plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(project(":service:module"))
    api(libs.dagger.dagger)
    implementation(libs.jakartaInject.api)
    implementation(libs.jaxRs.api)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(project(":testing"))
    testImplementation(libs.dropwizard.core)
    testImplementation(libs.dropwizard.testing)
    testImplementation(libs.okhttp.okhttp)
}
