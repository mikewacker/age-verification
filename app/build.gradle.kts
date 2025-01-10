plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(libs.dropwizard.core)
    implementation(project(":api"))
    implementation(project(":module:client"))
    implementation(project(":module:crypto-demo"))
    implementation(project(":module:request-demo"))
    implementation(project(":module:store-demo"))
    implementation(project(":module:store-inmemory"))
    implementation(project(":service"))
    implementation(project(":service:api"))
    implementation(libs.dagger.dagger)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.databind)
    implementation(libs.jakartaInject.api)
    implementation(libs.jakartaValidation.api)

    testImplementation(project(":testing"))
    testImplementation(libs.dropwizard.testing)
    testImplementation(libs.okhttp.okhttp)
    testImplementation(libs.retrofit.retrofit)
}
