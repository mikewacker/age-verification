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

    implementation(libs.drift.api)
    implementation(libs.undertow.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":core:api:extractors"))
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.javaxInject.inject)
    testImplementation(libs.mockito.core)
    testImplementation(libs.undertow.core)
}
