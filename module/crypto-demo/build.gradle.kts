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
    implementation(project(":module:common"))
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.jaxRs)
    implementation(libs.bundles.json)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":service:module")))
    testImplementation(testFixtures(project(":module:common")))
}
