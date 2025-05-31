plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(project(":service:module"))
    api(project(":module:common"))
    api(libs.bundles.dagger)
    implementation(libs.bundles.jaxRs)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":service:module")))
    testImplementation(testFixtures(project(":module:common")))
    testImplementation(libs.bundles.dropwizard)
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
