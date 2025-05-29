plugins {
    `java-library`
    `java-test-fixtures`
    id("org.example.age.java-conventions")
}

dependencies {
    api(libs.immutables.annotations)
    api(libs.retrofit.retrofit)
    implementation(libs.jaxRs.api)

    testFixturesApi(libs.jackson.databind)
    testFixturesApi(libs.okhttp.okhttp)
    testFixturesApi(libs.retrofit.retrofit)
    testFixturesImplementation(libs.assertj.core)
    testFixturesImplementation(libs.dropwizard.jackson)
    testFixturesImplementation(libs.jaxRs.api)
    testFixturesImplementation(libs.retrofit.mock)
    testFixturesImplementation(libs.retrofit.converterJackson)

    testImplementation(libs.dropwizard.testing)
    testImplementation(libs.retrofit.mock)
}
