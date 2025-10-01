plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(project(":common:spi"))
    api(libs.bundles.retrofit)
    implementation(project(":testing"))
    implementation(libs.bundles.jaxRs)
    implementation(libs.assertj.core)
    implementation(libs.junitJupiter.api)
}
