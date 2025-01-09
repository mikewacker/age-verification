plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(project(":api"))
    api(libs.dagger.dagger)
    api(libs.dropwizard.core)
    api(libs.jackson.databind)
    api(libs.okhttp.okhttp)
    api(libs.retrofit.retrofit)
    implementation(libs.assertj.core)
    implementation(libs.jackson.datatypeJsr310)
    implementation(libs.jakartaInject.api)
    implementation(libs.jaxRs.api)
    implementation(libs.retrofit.mock)
}
