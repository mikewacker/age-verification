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
    testFixturesImplementation(project(":testing"))
    testFixturesImplementation(libs.bundles.jaxRs)
    testFixturesImplementation(libs.assertj.core)
}
