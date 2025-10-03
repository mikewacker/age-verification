plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    implementation(project(":common:env"))
    implementation(libs.dagger.dagger)
    implementation(libs.dropwizard.core)
    implementation(libs.jackson.databind)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":common:api"))
    testImplementation(project(":testing"))
    testImplementation(libs.dropwizard.testing)
}
