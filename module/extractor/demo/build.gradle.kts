plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)

    api(project(":core:api:extractors"))
    api(libs.dagger.dagger)
    api(libs.javaxInject.inject)

    implementation(project(":base:api:base"))
    implementation(libs.undertow.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:extractors"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.javaxInject.inject)
    testImplementation(libs.mockito.core)
    testImplementation(libs.undertow.core)
}
