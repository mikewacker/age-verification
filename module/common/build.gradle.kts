plugins {
    `java-library`
    `java-test-fixtures`
    id("buildlogic.java-conventions")
}

dependencies {
    testFixturesApi(platform(libs.junit.bom))
    testFixturesApi(libs.junitJupiter.api)
}
