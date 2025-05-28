plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(project(":common"))
    api(project(":api"))
    api(libs.dagger.dagger)
    api(libs.immutables.annotations)
    api(libs.jaxRs.api)
    implementation(libs.jakartaInject.api)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(project(":testing"))
    testImplementation(libs.okhttp.okhttp)
    testImplementation(libs.dropwizard.core)
    testImplementation(libs.dropwizard.testing)
}
