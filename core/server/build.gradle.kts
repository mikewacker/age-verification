plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)

    api(libs.dagger.dagger)
    api(libs.javaxInject.inject)
    api(libs.undertow.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(libs.dagger.dagger)
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.drift)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jackson.core)
    testImplementation(libs.javaxInject.inject)
    testImplementation(libs.okhttp3.okhttp)
    testImplementation(libs.undertow.core)
}
