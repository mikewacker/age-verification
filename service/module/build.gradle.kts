plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    api(project(":api"))
    implementation(libs.jaxRs.api)

    testFixturesApi(libs.junitJupiter.api)
    testFixturesImplementation(testFixtures(project(":common")))
    testFixturesImplementation(project(":api"))
    testFixturesImplementation(testFixtures(project(":api")))
    testFixturesImplementation(libs.assertj.core)
}
