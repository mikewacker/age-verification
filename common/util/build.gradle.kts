plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    api(platform(libs.retrofit.bom))
    api(libs.retrofit.retrofit)

    implementation(platform(libs.immutables.bom))
    implementation(libs.immutables.annotations)

    testImplementation(libs.retrofit.mock)
}
