plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.dagger.compiler)

    api(project(":service"))
    api(project(":module:client"))
    api(project(":module:store-redis"))
    api(project(":module:crypto-demo"))
    api(libs.bundles.dropwizard)
    api(libs.bundles.json)

    implementation(project(":api"))
    implementation(project(":service:module"))
    implementation(project(":module:common"))
    implementation(project(":module:request-demo"))
    implementation(libs.bundles.redis) // for Dagger

    testImplementation(testFixtures(project(":common")))
    testImplementation(project(":testing"))
    testImplementation(project(":test-containers"))
    testImplementation(libs.bundles.retrofit)
    testImplementation(libs.dropwizard.testing)
}
