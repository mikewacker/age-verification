plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesImplementation(testFixtures(project(":common")))
    testFixturesImplementation(project(":api"))
    testFixturesImplementation(testFixtures(project(":api")))
    testFixturesImplementation(project(":service:module"))
    testFixturesImplementation(libs.bundles.dagger)
    testFixturesImplementation(libs.bundles.jaxRs)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":service:module")))
    testImplementation(libs.bundles.dagger)
    testImplementation(libs.bundles.jaxRs)
}
