plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    api(project(":core:api:types"))
    api(project(":core:api:extractors"))
    api(libs.dagger.dagger)
    api(libs.drift.api)
    api(libs.drift.drift)
    api(libs.immutables.annotations)
    api(libs.jackson.annotations)
    api(libs.jackson.databind)
    api(libs.jakartaInject.api)
    api(libs.undertow.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":core:api:types"))
    testImplementation(project(":core:api:extractors"))
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jackson.core)
    testImplementation(libs.jakartaInject.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.undertow.core)
}
