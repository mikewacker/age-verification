plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    api(libs.bundles.json)
    api(libs.bundles.retrofit)
    implementation(libs.bundles.jaxRs)

    testFixturesApi(libs.bundles.json)
    testFixturesApi(libs.bundles.retrofit)
    testFixturesImplementation(libs.bundles.jaxRs)
    testFixturesImplementation(libs.assertj.core)
    testFixturesImplementation(libs.dropwizard.jackson)
    testFixturesImplementation(libs.retrofit.mock)

    testFixturesRuntimeOnly(libs.dropwizard.jersey) // provides RuntimeDelegate for JAX-RS response

    testImplementation(libs.dropwizard.testing)
    testImplementation(libs.retrofit.mock)
}
