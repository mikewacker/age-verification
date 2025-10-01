plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesImplementation(project(":common:api"))
    testFixturesImplementation(project(":common:spi"))
    testFixturesImplementation(project(":service:module"))
    testFixturesImplementation(project(":testing"))
    testFixturesImplementation(libs.bundles.dagger)
    testFixturesImplementation(libs.bundles.jaxRs)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
    testImplementation(project(":common:spi"))
    testImplementation(testFixtures(project(":service:module")))
    testImplementation(libs.bundles.dagger)
    testImplementation(libs.bundles.jaxRs)
}
