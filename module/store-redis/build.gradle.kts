plugins {
    `java-library`
    `java-test-fixtures`
    id("buildlogic.java-conventions")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    implementation(project(":site:spi"))
    implementation(project(":avs:spi"))
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
    testImplementation(project(":common:spi-testing"))
    testImplementation(testFixtures(project(":service:module")))
    testImplementation(testFixtures(project(":module:common")))
}

dockerCompose {
    isRequiredBy(tasks.test)
}
