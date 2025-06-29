plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    implementation(project(":service:module"))
    implementation(project(":module:common"))
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.jaxRs)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":service:module")))
    testImplementation(libs.bundles.dropwizard)
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
