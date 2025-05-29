plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(libs.dagger.dagger)
    api(libs.jackson.databind)
    api(libs.okhttp.okhttp)
    api(libs.retrofit.retrofit)
    implementation(testFixtures(project(":common")))
    implementation(libs.assertj.core)
    implementation(libs.dropwizard.core)
    implementation(libs.jakartaInject.api)
    implementation(libs.jaxRs.api)
    implementation(libs.retrofit.converterJackson)
    implementation(libs.retrofit.mock)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(libs.dropwizard.testing)
    testImplementation(libs.jackson.annotations)
}
