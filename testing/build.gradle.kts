plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(libs.dropwizard.core)
    api(libs.jackson.databind)
    api(libs.okhttp.okhttp)
    api(libs.retrofit.retrofit)
    implementation(libs.assertj.core)
    implementation(libs.jackson.datatypeJsr310)
    implementation(libs.jaxRs.api)
    implementation(libs.retrofit.mock)
}
