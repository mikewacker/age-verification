plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    api(project(":base:data:json"))
    api(project(":base:api:base"))
    api(project(":core:api:types"))
    api(project(":core:api:extractors"))
    api(libs.dagger.dagger)
    api(libs.immutables.annotations)
    api(libs.jackson.annotations)
    api(libs.jackson.databind)
    api(libs.javaxInject.inject)
    api(libs.undertow.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":core:api:extractors"))
    testImplementation(testFixtures(project(":base:data:json")))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.jackson.core)
    testImplementation(libs.javaxInject.inject)
    testImplementation(libs.mockito.core)
    testImplementation(libs.undertow.core)
}
