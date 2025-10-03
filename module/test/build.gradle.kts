plugins {
    `java-library`
    `java-test-fixtures`
    id("buildlogic.java-conventions")
}

dependencies {
    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesImplementation(project(":site:spi"))
    testFixturesImplementation(project(":avs:spi"))
    testFixturesImplementation(project(":testing"))
    testFixturesImplementation(libs.bundles.dagger)
    testFixturesImplementation(libs.jaxRs.api)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
    testImplementation(project(":common:spi"))
    testImplementation(project(":common:spi-testing"))
    testImplementation(testFixtures(project(":service:module")))
    testImplementation(libs.bundles.dagger)
}
