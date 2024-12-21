plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)

    api(project(":core:service:types"))
    api(libs.dagger.dagger)
    api(libs.jakartaInject.api)

    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":core:api:types"))
    implementation(libs.drift.api)
    implementation(libs.guava.guava)
    implementation(libs.jackson.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":core:service:types"))
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.drift.api)
    testImplementation(libs.drift.testlib)
    testImplementation(libs.jakartaInject.api)
}
