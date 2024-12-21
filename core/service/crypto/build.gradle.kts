plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)

    api(project(":crypto:data"))
    api(project(":core:data"))
    api(project(":core:api:types"))
    api(libs.dagger.dagger)
    api(libs.drift.api)
    api(libs.jakartaInject.api)

    implementation(project(":core:service:types"))
    implementation(libs.jackson.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":module:extractor:builtin"))
    testImplementation(testFixtures(project(":module:service:test")))
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jakartaInject.api)
}
