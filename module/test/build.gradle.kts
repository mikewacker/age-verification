plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    testFixturesAnnotationProcessor(libs.dagger.compiler)

    testFixturesApi(project(":service:module"))
    testFixturesApi(libs.bundles.dagger)
    testFixturesImplementation(project(":api"))
    testFixturesImplementation(testFixtures(project(":api")))
    testFixturesImplementation(libs.bundles.jaxRs)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":service:module")))
}
