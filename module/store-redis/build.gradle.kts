plugins {
    `java-library`
    `java-test-fixtures`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    implementation(project(":site:spi"))
    implementation(project(":avs:spi"))
    implementation(project(":common:env"))
    implementation(libs.jedis.jedis)

    testFixturesApi(project(":common:api"))
    testFixturesApi(testFixtures(project(":module:common")))
    testFixturesApi(libs.jedis.jedis)
    testFixturesImplementation(project(":testing"))

    testImplementation(project(":common:spi-testing"))
    testImplementation(project(":service:module"))
}

dockerCompose {
    isRequiredBy(tasks.test)
}
