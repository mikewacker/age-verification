plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":site:spi"))
    api(project(":avs:spi"))
    api(libs.bundles.retrofit)
    api(libs.junitJupiter.api)
    implementation(project(":testing"))
    implementation(libs.assertj.core)
}
