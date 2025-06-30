plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    api(project(":api"))
    implementation(libs.jaxRs.api)

    testFixturesApi(project(":api"))
    testFixturesApi(libs.bundles.retrofit)
    testFixturesApi(libs.junitJupiter.api)
    testFixturesImplementation(testFixtures(project(":common")))
    testFixturesImplementation(testFixtures(project(":api")))
    testFixturesImplementation(libs.bundles.jaxRs)
    testFixturesImplementation(libs.assertj.core)
}
