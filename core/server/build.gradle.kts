plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)

    api(libs.dagger.dagger)
    api(libs.jakartaInject.api)
    api(libs.undertow.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(libs.dagger.dagger)
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.drift)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jackson.core)
    testImplementation(libs.jakartaInject.api)
    testImplementation(libs.okhttp.okhttp)
    testImplementation(libs.undertow.core)
}
