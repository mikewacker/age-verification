plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    implementation(project(":common:util"))
    implementation(project(":site:api"))
    implementation(project(":avs:api"))
    implementation(project(":common:spi"))
    implementation(project(":service:module"))
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.jaxRs)
    implementation(libs.bundles.json)
    implementation(libs.bundles.retrofit)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
    testImplementation(testFixtures(project(":module:test")))
    testImplementation(libs.retrofit.mock)
}
