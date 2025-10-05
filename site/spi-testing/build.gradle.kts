plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":site:spi"))

    implementation(platform(libs.assertj.bom))
    implementation(platform(libs.junit.bom))
    implementation(project(":testing"))
    implementation(libs.assertj.core)
    implementation(libs.junitJupiter.api)
}
