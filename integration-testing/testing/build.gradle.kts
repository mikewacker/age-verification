plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":site:api"))
    api(project(":avs:api"))

    implementation(platform(libs.assertj.bom))
    implementation(platform(libs.junit.bom))
    implementation(libs.assertj.core)
    implementation(libs.junitJupiter.api)
}
