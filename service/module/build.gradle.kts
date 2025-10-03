plugins {
    `java-library`
    `java-test-fixtures`
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":site:api"))
    implementation(libs.jaxRs.api)

    testFixturesApi(project(":site:api"))
    testFixturesApi(project(":avs:api"))
    testFixturesApi(libs.bundles.retrofit)
    testFixturesApi(libs.junitJupiter.api)
    testFixturesImplementation(project(":testing"))
    testFixturesImplementation(libs.assertj.core)
    testFixturesImplementation(libs.jaxRs.api)
}
