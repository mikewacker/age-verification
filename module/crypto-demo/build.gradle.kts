plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    api(project(":common"))
    api(project(":service:module"))
    api(project(":module:common"))
    api(libs.bundles.dagger)
    api(libs.bundles.json)
    implementation(project(":api"))
    implementation(libs.bundles.jaxRs)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":service:module")))
    testImplementation(testFixtures(project(":module:common")))
}
