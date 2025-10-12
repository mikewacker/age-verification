plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
}

dependencies {
    api(project(":avs:api"))
    api(project(":common:spi"))
}
