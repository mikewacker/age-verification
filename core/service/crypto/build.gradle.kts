plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)

    api(project(":crypto:data"))
    api(project(":core:data"))
    api(project(":base:api:base"))
    api(project(":core:api:types"))
    api(libs.dagger.dagger)
    api(libs.javaxInject.inject)

    implementation(project(":base:data:json"))
    implementation(project(":core:service:types"))
    implementation(libs.jackson.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":module:extractor:builtin"))
    testImplementation(testFixtures(project(":module:service:test")))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.javaxInject.inject)
}
