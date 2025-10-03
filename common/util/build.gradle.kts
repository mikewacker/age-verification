plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    api(libs.bundles.retrofit)
    api(libs.immutables.annotations) // see: https://bugs.openjdk.org/browse/JDK-8305250

    testImplementation(project(":testing"))
    testImplementation(libs.retrofit.mock)
}
