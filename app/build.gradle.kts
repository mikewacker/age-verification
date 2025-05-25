plugins {
    application
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    implementation(project(":api"))
    implementation(project(":module:client"))
    implementation(project(":module:crypto-demo"))
    implementation(project(":module:request-demo"))
    implementation(project(":module:store-redis"))
    implementation(project(":service"))
    implementation(project(":service:module"))
    implementation(project(":testing")) // for demo
    implementation(libs.dagger.dagger)
    implementation(libs.dropwizard.core)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.databind)
    implementation(libs.jakartaInject.api)
    implementation(libs.jakartaValidation.api)
    implementation(libs.okhttp.okhttp)
    implementation(libs.retrofit.retrofit)

    testImplementation(project(":test-containers"))
    testImplementation(libs.dropwizard.testing)
}

application {
    mainClass = "org.example.age.demo.Demo"
}
