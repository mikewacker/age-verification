plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    api(platform(libs.jackson.bom))
    api(libs.jacksonCore.databind)
}
