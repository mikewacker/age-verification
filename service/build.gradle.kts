plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    api(project(":common"))
    api(project(":api"))
    api(project(":service:module"))
    api(libs.bundles.dagger)
    api(libs.bundles.json)
    implementation(libs.bundles.jaxRs)
    implementation(libs.bundles.retrofit)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":module:test")))
    testImplementation(libs.retrofit.mock)
}
