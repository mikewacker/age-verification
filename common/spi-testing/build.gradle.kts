plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":common:spi"))
    api(libs.retrofit.retrofit)
    implementation(project(":testing"))
    implementation(libs.assertj.core)
    implementation(libs.junitJupiter.api)
}
