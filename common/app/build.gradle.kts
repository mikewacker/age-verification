plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    implementation(project(":common:env"))
    implementation(libs.dagger.dagger)
    implementation(libs.dropwizard.core)
    implementation(libs.jackson.databind)
}
