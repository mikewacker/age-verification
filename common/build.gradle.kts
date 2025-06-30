plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    api(libs.bundles.retrofit)
    api(libs.immutables.annotations) // see: https://bugs.openjdk.org/browse/JDK-8305250
    implementation(libs.bundles.jaxRs)

    testFixturesApi(libs.bundles.json)
    testFixturesApi(libs.bundles.retrofit)
    testFixturesImplementation(libs.bundles.dropwizard) // also provides RuntimeDelegate for JAX-RS response
    testFixturesImplementation(libs.bundles.jaxRs)
    testFixturesImplementation(libs.assertj.core)
    testFixturesImplementation(libs.retrofit.mock)

    testImplementation(libs.dropwizard.testing)
    testImplementation(libs.retrofit.mock)
}
