plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(project(":service"))
    api(project(":module:client"))
    api(project(":module:store-redis"))
    api(project(":module:crypto-demo"))
    api(libs.dropwizard.core)
    api(libs.jackson.annotations)

    implementation(project(":api"))
    implementation(project(":service:module"))
    implementation(project(":module:request-demo"))
    implementation(libs.dagger.dagger)
    implementation(libs.jackson.databind)
    implementation(libs.jakartaInject.api)
    implementation(libs.jakartaValidation.api)
    implementation(libs.jedis.jedis) // for Dagger

    testImplementation(testFixtures(project(":common")))
    testImplementation(project(":testing"))
    testImplementation(project(":test-containers"))
    testImplementation(libs.dropwizard.testing)
    testImplementation(libs.okhttp.okhttp)
    testImplementation(libs.retrofit.retrofit)
}
