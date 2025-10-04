plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    implementation(project(":site:spi"))
    implementation(project(":avs:spi"))
    implementation(project(":testing"))
    implementation(libs.bundles.dagger)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
    testImplementation(project(":common:spi"))
    testImplementation(project(":common:spi-testing"))
    testImplementation(project(":service:module"))
    testImplementation(libs.bundles.dagger)
}
