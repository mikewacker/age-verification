plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    implementation(project(":common:spi"))
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.darc)
    implementation(libs.bundles.jaxRs)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":common:spi-testing"))
    testImplementation(libs.bundles.dropwizard)
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
