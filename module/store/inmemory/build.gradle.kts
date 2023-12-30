plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor(libs.dagger.compiler)

    api(project(":core:service:types"))
    api(libs.dagger.dagger)
    api(libs.javaxInject.inject)

    implementation(project(":base:data:json"))
    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":base:api:base"))
    implementation(project(":core:api:types"))
    implementation(libs.guava.guava)
    implementation(libs.jackson.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":core:service:types"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.javaxInject.inject)
}
