plugins {
    `java-library`
    `java-test-fixtures`
    id("buildlogic.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    implementation(project(":common:util"))
    implementation(project(":common:api"))
    implementation(project(":service:module"))
    implementation(project(":common:env"))
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.json)
    implementation(libs.bundles.redis)

    testFixturesApi(testFixtures(project(":module:common")))
    testFixturesApi(libs.bundles.redis)
    testFixturesImplementation(project(":common:api"))
    testFixturesImplementation(project(":testing"))

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(project(":testing"))
    testImplementation(testFixtures(project(":service:module")))
    testImplementation(testFixtures(project(":module:common")))
}
