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
    implementation(project(":core:api:types"))
    implementation(project(":module:store:inmemory"))
    implementation(libs.bouncycastle.pkix)
    implementation(libs.bouncycastle.prov)
    implementation(libs.jackson.core)

    // test
    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":core:service:types"))
    testImplementation(project(":module:store:inmemory")) // Dagger component
    testImplementation(libs.bouncycastle.prov)
    testImplementation(libs.dagger.dagger)
    testImplementation(libs.jackson.core)
    testImplementation(libs.javaxInject.inject)
}
