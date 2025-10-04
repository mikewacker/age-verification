plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    compileOnly(platform(libs.immutables.bom))
    compileOnly(libs.immutables.annotations)
}
