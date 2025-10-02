plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(libs.jackson.databind)
    implementation(libs.dagger.dagger)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
}
