plugins {
    `java-test-fixtures`
    id("buildlogic.java-conventions")
}

dependencies {
    testFixturesApi(project(":site:spi"))
    testFixturesApi(project(":avs:spi"))
    testFixturesApi(libs.bundles.retrofit)
    testFixturesApi(libs.junitJupiter.api)
    testFixturesImplementation(project(":testing"))
    testFixturesImplementation(libs.assertj.core)
    testFixturesImplementation(libs.jaxRs.api)
}
