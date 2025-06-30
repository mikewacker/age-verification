plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)
    annotationProcessor(libs.immutables.value)

    implementation(project(":common"))
    implementation(project(":api"))
    implementation(project(":service:module"))
    implementation(project(":module:common"))
    implementation(libs.bundles.dagger)
    implementation(libs.bundles.json)
    implementation(libs.bundles.redis)

    testFixturesApi(testFixtures(project(":module:common")))
    testFixturesApi(libs.bundles.redis)
    testFixturesImplementation(testFixtures(project(":common")))
    testFixturesImplementation(project(":api"))

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(testFixtures(project(":common")))
    testImplementation(testFixtures(project(":api")))
    testImplementation(testFixtures(project(":service:module")))
    testImplementation(testFixtures(project(":module:common")))
}
