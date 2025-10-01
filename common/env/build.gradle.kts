plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(libs.bundles.json)
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.dropwizard)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
}
