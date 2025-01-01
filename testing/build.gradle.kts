plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(libs.jackson.databind)
    api(libs.retrofit.mock)
    implementation(libs.assertj.core)
    implementation(libs.jackson.datatypeJsr310)
    implementation(libs.jaxRs.api)
    implementation(libs.okhttp.okhttp)
    implementation(libs.retrofit.mock)
}
