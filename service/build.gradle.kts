plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    implementation(project(":common"))
    implementation(project(":api"))
    implementation(project(":service:module"))
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.jaxRs)
    implementation(libs.bundles.json)
    implementation(libs.bundles.retrofit)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":module:test")))
    testImplementation(libs.retrofit.mock)
}
