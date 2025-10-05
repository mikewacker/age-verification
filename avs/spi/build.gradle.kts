plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":avs:api"))
    api(project(":common:spi"))
}
